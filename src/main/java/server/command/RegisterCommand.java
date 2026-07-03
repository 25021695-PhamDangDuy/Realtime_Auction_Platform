package server.command;

import service.brain.AccountController;
import server.ClientSession;
import server.Role;

import java.util.Set;

public class RegisterCommand implements Command {
    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.GUEST,Role.BIDDER,Role.SELLER,Role.ADMIN); // Chỉ khách mới được đăng ký
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp dự kiến: REGISTER|username|password|confirmPassword
        if (args.length < 4) {
            session.sendMessage("ERROR|Thiếu tham số đăng ký.");
            return;
        }

        String name = args[1];
        String pw = args[2];
        String idPW = args[3];


        try {
            // Gọi Lõi nghiệp vụ
            AccountController.getInstance().Register( name, pw, idPW);

            // Nếu không bị văng lỗi gì, tức là thành công
            session.sendMessage("SUCCESS|Đăng ký thành công! Vui lòng đăng nhập.");

        } catch (Exception e) {
            // Nếu "Mật khẩu không đủ mạnh", nó sẽ hiện ở đây
            session.sendMessage("ERROR|" + e.getMessage());
        }
    }
}
