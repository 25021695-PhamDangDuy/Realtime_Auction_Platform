package server.command;

import controller.ItemController.ItemController;
import models.Item;
import server.ClientSession;
import server.Role;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class GET_LIST_ITEM implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.SELLER);
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
            if (myItems == null || myItems.isEmpty()) {
                session.sendMessage("SUCCESS_MY_ITEMS|EMPTY");
                return;
            }

            // Gói ghém dữ liệu (ID, Tên, Giá)
            StringBuilder response = new StringBuilder("SUCCESS_MY_ITEMS");
            for (Item item : myItems) {
                response.append("|")
                        .append(item.getID()).append(",")
                        .append(item.getName()).append(",")
                        .append(item.getPrice());
            }

            // Gửi trả kết quả cho Client
            session.sendMessage(response.toString());
            System.out.println("[Command] Đã gửi danh sách " + status + " cho Seller: " + ownerName);

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