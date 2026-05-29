package myWeb.server.command;

import myWeb.server.ClientSession;
import myWeb.server.Role;
import myWeb.controller.AccountController;

import java.util.Set;
import java.util.UUID;

public class RegisterCommand implements Command {
    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.GUEST); // Chỉ khách mới được đăng ký
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

        // Tạo một ID ngẫu nhiên cho người dùng mới
        String newId = "U_" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        try {
            // Gọi Lõi nghiệp vụ (logic của đồng đội bạn)
            AccountController.getInstance().Register(newId, name, pw, idPW);

            // Nếu không bị văng lỗi gì, tức là thành công
            session.sendMessage("SUCCESS|Đăng ký thành công! Vui lòng đăng nhập.");

        } catch (Exception e) {
            // Nếu đồng đội bạn quăng lỗi "Mật khẩu không đủ mạnh", nó sẽ hiện ở đây!
            session.sendMessage("ERROR|" + e.getMessage());
        }
    }
}