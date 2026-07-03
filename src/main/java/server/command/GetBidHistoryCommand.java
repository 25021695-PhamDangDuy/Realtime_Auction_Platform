package server.command;

import service.BidHistory;
import server.ClientSession;
import models.BidTicket;
import server.GsonUtil;
import server.Role;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GetBidHistoryCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        // Cả người mua, người bán và Admin đều có quyền xem lịch sử đấu giá cho minh bạch
        return Set.of(Role.BIDDER, Role.SELLER, Role.ADMIN);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp chuẩn Client gửi lên: GET_BID_HISTORY | <Session_ID>
        if (args.length < 2) {
            session.sendMessage("ERROR|Thiếu ID phòng đấu giá để xem lịch sử.");
            return;
        }

        try {
            UUID roomId = UUID.fromString(args[1]);


            // 2. LẤY DANH SÁCH LỊCH SỬ TỪ DATABASE HOẶC MEMORY

            List<BidTicket> history = BidHistory.getLegalBySessionID(roomId);
            // 3. Kiểm tra nếu phòng chưa có ai đặt giá
            if (history == null || history.isEmpty()) {
                session.sendMessage("SUCCESS_BID_HISTORY|EMPTY");
                return;
            }

            // ========================================================
            // 4. GÓI GHÉM BẰNG GSON VÀ GỬI VỀ CLIENT
            // ========================================================
            // GSON sẽ nén toàn bộ cái mảng List<BidTicket> này thành 1 cục JSON
            String jsonHistory = GsonUtil.gson.toJson(history);

            session.sendMessage("SUCCESS_BID_HISTORY|" + jsonHistory);

            System.out.println("[Command] Đã gửi lịch sử đấu giá phòng " + roomId + " cho " + session.getCurrentUser().getName());

        } catch (IllegalArgumentException e) {
            session.sendMessage("ERROR|Mã phòng đấu giá không hợp lệ.");
        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi hệ thống khi tải lịch sử đấu giá.");
            e.printStackTrace();
        }
    }
}
