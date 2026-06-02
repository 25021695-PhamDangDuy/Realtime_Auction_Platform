package server.command;


import controller.ItemController.ItemController;
import models.Item;
import server.ClientSession;
import server.GsonUtil;
import server.Role;

import java.util.Set;
import java.util.UUID;

public class GetItemDetailCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        // Lệnh này xài chung cho toàn bộ hệ thống
        return Set.of(Role.BIDDER, Role.SELLER, Role.ADMIN);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp: GET_ITEM_DETAIL | MODE | ID
        if (args.length < 3) {
            session.sendMessage("ERROR|Thiếu tham số để lấy chi tiết sản phẩm.");
            return;
        }

        try {
            String mode = args[1].toUpperCase(); // "BY_ITEM_ID" hoặc "BY_SESSION_ID"
            String targetId = args[2];

            Item targetItem = null;

            // ========================================================
            // ĐỊNH TUYẾN THEO CÁCH TÌM KIẾM
            // ========================================================
            switch (mode) {
                case "BY_ITEM_ID":
                    UUID itemId = UUID.fromString(targetId);

                    targetItem = ItemController.getByUUID(itemId);
                    break;

                case "BY_SESSION_ID":
                    UUID sessionId=UUID.fromString(targetId);
                    targetItem = ItemController.getBySession(sessionId);
                    break;

                default:
                    session.sendMessage("ERROR|Phương thức tìm kiếm không hợp lệ: " + mode);
                    return;
            }// Kiểm tra xem món hàng có tồn tại không
            if (targetItem == null) {
                session.sendMessage("ERROR|Sản phẩm không tồn tại hoặc đã bị xóa.");
                return;
            }

            // ========================================================
            // GÓI GHÉM THÔNG TIN BẰNG GSON (TỰ ĐỘNG ĐA HÌNH)
            // ========================================================
            // GsonUtil.gson sẽ tự động chèn thêm cái mác "type":"ART" (hoặc VEHICLE...)
            // dựa vào class thực sự của targetItem lúc chạy.
            String jsonItem = GsonUtil.gson.toJson(targetItem);

            String response = "SUCCESS_ITEM_DETAIL|" + jsonItem;

            session.sendMessage(response);
            System.out.println("[Command] Đã gửi chi tiết sản phẩm cho: " + session.getCurrentUser().getName());

        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi hệ thống khi tải chi tiết sản phẩm.");
            e.printStackTrace();
        }
    }
}