package server.command;

import service.brain.AccountController;
import server.ClientSession;
import models.User;
import server.GsonUtil;
import server.Role;
// import dao.UserDAO; // Nhớ import class thao tác Database của bạn
// import utils.GsonUtil;

import java.util.Set;

public class GetInformationCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        // Cho phép tất cả những người dùng ĐÃ ĐĂNG NHẬP được xem thông tin
        return Set.of(Role.BIDDER, Role.SELLER, Role.ADMIN);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp 1: GET_INFORMATION (Tự lấy profile của mình)
        // Cú pháp 2: GET_INFORMATION | <tên_đăng_nhập_của_người_khác>

        try {
            User targetUser = null;

            if (args.length >= 2) {
                // Trường hợp muốn soi profile của người khác (VD: Bidder xem uy tín của Seller)
                String targetUsername = args[1];

                // GỌI HÀM DATABASE Ở ĐÂY
                targetUser= AccountController.getInstance().getInfor(targetUsername);

            } else {
                // Trường hợp tự bấm vào avatar của chính mình
                User preUser = session.getCurrentUser();
                targetUser = AccountController.getInstance().getInfor(preUser.getID());
            }

            // Kiểm tra tồn tại
            if (targetUser == null) {
                session.sendMessage("ERROR|Không tìm thấy thông tin người dùng này.");
                return;
            }
            // ĐÓNG GÓI JSON ĐA HÌNH VỚI GSON
            // Gson sẽ tự động chèn cái nhãn "role":"BIDDER" hoặc "SELLER" vào chuỗi
            String jsonProfile = GsonUtil.gson.toJson(targetUser, User.class);

            session.sendMessage("SUCCESS_INFORMATION|" + jsonProfile);
            System.out.println("[Command] Đã gửi thông tin User bằng GSON cho: " + session.getCurrentUser().getName());

        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi hệ thống khi tải thông tin cá nhân.");
            e.printStackTrace();
        }
    }
}