package server.command;

import controller.ItemService.ItemController;
import models.Item;
import server.ClientSession;
import server.GsonUtil;
import server.Role;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GetListItemCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.SELLER,Role.BIDDER,Role.ADMIN);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp chuẩn Client gửi lên: GET_MY_ITEMS | TRẠNG_THÁI
        // (Trạng thái gồm: ALL, SOLD, AVAILABLE, AUCTION)
        if (args.length < 2) {
            session.sendMessage("ERROR|Thiếu trạng thái sản phẩm cần lấy.");
            return;
        }

        try {
            String status = args[1].toUpperCase();

            // Lấy tên đăng nhập duy nhất của Seller từ Session
            String ownerName = session.getCurrentUser().getName();

            List<Item> myItems = null;

            // ========================================================
            // GỌI CHÍNH XÁC CÁC HÀM TRONG ITEM CONTROLLER CỦA BẠN DUY
            // ========================================================
            switch (status) {
                case "ALL":
                    myItems = ItemController.getAllItemByOwnername(ownerName);
                    break;
                case "SOLD":
                    myItems = ItemController.getAll_SOLD_byOwnername(ownerName);
                    break;
                case "AVAILABLE":
                    myItems = ItemController.getAll_AVAILABLE_byOwnername(ownerName);
                    break;
                case "AUCTION":
                    myItems = ItemController.getAll_AUCTION_byOwnername(ownerName);
                    break;
                default:
                    session.sendMessage("ERROR|Trạng thái không hợp lệ: " + status);
                    return;
            }

            // Xử lý nếu kho rỗng
            // 1. CHỐNG NULL: Nếu Database không tìm thấy món nào, trả về danh sách rỗng
            // chứ tuyệt đối không được để myItems = null, GSON sẽ không nén được thành mảng []
            if (myItems == null) {
                myItems = new ArrayList<>();
            }

            // 2. ÉP GSON NÉN THÀNH DẠNG MẢNG (ARRAY)
            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<models.Item>>(){}.getType();
            String jsonList = GsonUtil.gson.toJson(myItems, listType);

            // 3. GỬI VỀ CLIENT
            session.sendMessage("SUCCESS_GET_ITEMS|" + jsonList);

            System.out.println("[Command] Đã gửi danh sách Item bằng GSON cho Seller: " + ownerName);

        } catch (SQLException e) {
            // Bắt lỗi Database riêng biệt do DAO ném ra
            session.sendMessage("ERROR|Lỗi truy xuất cơ sở dữ liệu khi tải kho đồ.");
            System.err.println("[SQL_ERROR] Lỗi khi lấy danh sách Item: " + e.getMessage());
        } catch (Exception e) {
            // Bắt các lỗi hệ thống khác
            session.sendMessage("ERROR|Lỗi hệ thống không xác định.");
            e.printStackTrace();
        }
    }
}