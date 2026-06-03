package server.command;

import controller.brain.AccountController;
import models.Bidder;
import models.Seller;
import models.User;
import server.ClientSession;
import server.GsonUtil;
import server.Role;

import java.util.Set;

public class LoginCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        // Chỉ những người mang thẻ GUEST (chưa đăng nhập) mới được xài lệnh này
        return Set.of(Role.GUEST,Role.BIDDER,Role.SELLER,Role.ADMIN);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp Client gửi lên: LOGIN|username|password
        if (args.length < 3) {
            session.sendMessage("ERROR|Thiếu tham số đăng nhập.");
            return;
        }

        String username = args[1];
        String password = args[2];

        try {
            // 1. Gọi Lõi nghiệp vụ (Core Logic) để kiểm tra
            AccountController accountCtrl = AccountController.getInstance();
            User loggedInUser = accountCtrl.Login(username, password);

            // 2. Nếu code chạy xuống được đây nghĩa là không bị văng Exception (Đăng nhập đúng)
            session.setCurrentUser(loggedInUser);
            String userJson = GsonUtil.gson.toJson(loggedInUser,User.class);

            // 3. Phân loại người dùng bằng toán tử instanceof của Java
            if (loggedInUser instanceof Seller) {
                session.setRole(Role.SELLER);
                session.sendMessage("SUCCESS_LOGIN|" + userJson);
            } else if (loggedInUser instanceof Bidder) {
                session.setRole(Role.BIDDER);
                session.sendMessage("SUCCESS_LOGIN|" + userJson);
            } else {
                // Đề phòng có class Admin kế thừa User
                session.setRole(Role.ADMIN);
                session.sendMessage("SUCCESS_LOGIN|" + userJson);
            }

        } catch (Exception e) {
            // Hứng cái Exception "Sai tài khoản hoặc mật khẩu!" từ AccountController ném ra
            session.sendMessage("ERROR|" + e.getMessage());
        }
    }
}
