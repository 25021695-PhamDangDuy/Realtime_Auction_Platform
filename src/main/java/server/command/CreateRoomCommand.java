package server.command;

import controller.ItemService.ItemController;
import controller.brain.AuctionManager;
import server.ClientSession;
import models.Seller;
import models.Item;
import models.AuctionSession;
import server.GsonUtil;
import server.Role;
// import controller.ItemController;
// import controller.AuctionManager;
// import server.ClientManager;
// import utils.GsonUtil;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class CreateRoomCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.SELLER); // Chỉ Seller mới được tạo phòng
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp: CREATE_ROOM | itemID | startPrice | minIncrement | startTime | endTime
        if (args.length < 6) {
            session.sendMessage("ERROR|Thiếu thông số để tạo phòng đấu giá.");
            return;
        }

        try {
            // 1. Bóc tách dữ liệu từ mảng chuỗi
            UUID itemId = UUID.fromString(args[1]);
            long startPrice = Long.parseLong(args[2]);
            long minIncrement = Long.parseLong(args[3]);

            // Client phải gửi ngày tháng theo chuẩn ISO (vd: 2026-06-02T15:30:00)
            LocalDateTime startTime = LocalDateTime.parse(args[4]);
            LocalDateTime endTime = LocalDateTime.parse(args[5]);

            // 2. Lấy thông tin Seller đang yêu cầu tạo phòng
            Seller currentSeller = (Seller) session.getCurrentUser();

            // 3. Chọc xuống DB lấy Item ra
            // Tùy vào tên hàm trong ItemController của nhóm bạn
            Item targetItem = ItemController.getByUUID(itemId);

            if (targetItem == null) {
                session.sendMessage("ERROR|Sản phẩm không tồn tại.");
                return;
            }

            // ========================================================
            // 4. GỌI HÀM NGHIỆP VỤ CỦA AUCTION MANAGER
            // ========================================================
            // Bắt buộc hàm này bên AuctionManager phải đổi sang return AuctionSession nhé!
            AuctionSession newSession = AuctionManager.getInstance().createSession(
                    targetItem, currentSeller, startPrice, minIncrement, endTime, startTime
            );

            if (newSession == null) {
                session.sendMessage("ERROR|Không thể tạo phiên đấu giá (Sai thời gian hoặc Item không hợp lệ).");
                return;
            }

            // ========================================================
            // 5. BÁO THÀNH CÔNG CHO SELLER
            // ========================================================
            session.sendMessage("SUCCESS_CREATE_ROOM|Tạo phòng đấu giá thành công!");

            // ========================================================
            // 6. BROADCAST - BÁO CHO TOÀN BỘ SẢNH (GSON XUẤT CHIẾN)
            // ========================================================
            String jsonNewRoom = GsonUtil.gson.toJson(newSession);
            String broadcastMessage = "NEW_ROOM_OPENED|" + jsonNewRoom;

            // Lặp qua tất cả những người đang kết nối vào Server
            /* for (ClientSession client : ClientManager.getInstance().getAllActiveClients()) {
                // Đẩy thông báo phòng mới cho những người là BIDDER (Người mua)
                if (client.getRole() == Role.BIDDER) {
                    client.sendMessage(broadcastMessage);
                }
            }
            */
            System.out.println("[Broadcast] Phòng đấu giá mới đã được phát sóng!");

        } catch (java.time.format.DateTimeParseException e) {
            session.sendMessage("ERROR|Định dạng thời gian không hợp lệ. Vui lòng thử lại.");
        } catch (NumberFormatException e) {
            session.sendMessage("ERROR|Giá tiền nhập vào phải là số hợp lệ.");
        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi hệ thống khi khởi tạo phòng.");
            e.printStackTrace();
        }
    }
}