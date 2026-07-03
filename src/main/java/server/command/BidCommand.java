package server.command;

import service.brain.AuctionManager;
import server.ClientSession;
import models.Bidder;
import models.AuctionSession;
import server.GsonUtil;
import server.Role;

import java.util.Set;
import java.util.UUID;

public class BidCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.BIDDER); // Chỉ người mua mới được đặt giá
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        try {
            UUID roomId = UUID.fromString(args[1]);
            long bidAmount = Long.parseLong(args[2]);

            // Lấy ông khách đang đòi đặt giá
            Bidder currentBidder = (Bidder) session.getCurrentUser();

            // Tìm cái phòng ổng muốn đặt
            AuctionSession targetRoom = AuctionManager.getInstance().getSession(roomId);

            if (targetRoom == null) {
                session.sendMessage("ERROR|Phòng đấu giá không tồn tại.");
                return;
            }

            // ========================================================
            // GỌI HÀM CỦA BẠN DUY (Nó sẽ lo hết vụ sinh BidTicket)
            // ========================================================
            // Nếu có lỗi (như giá quá thấp), hàm này sẽ ném ra IllegalArgumentException
            targetRoom.placeBid(currentBidder, bidAmount);

            // ========================================================
            // NẾU CODE CHẠY XUỐNG ĐÂY NGHĨA LÀ ĐẶT GIÁ THÀNH CÔNG!
            // Chuẩn bị loa phường để hét lên (Broadcast)
            // ========================================================

            // Dùng GSON đóng gói tờ Biên lai (BidTicket) mới nhất
            // topBid chính là cái tờ biên lai xịn nhất mà hàm placeBid vừa tạo ra
            String jsonTopBid = GsonUtil.gson.toJson(targetRoom.getTopbid());

            String broadcastMessage = "NEW_BID_UPDATE|" + jsonTopBid;

            // BẬT LOA!
            // Nó sẽ gửi luồng tin này cho TẤT CẢ những ai đã chạy lệnh JOIN_ROOM trước đó
            targetRoom.notifyBidObservers(broadcastMessage);

            // Báo riêng cho ông A biết là ổng vừa đặt giá thành công
            session.sendMessage("SUCCESS_BID|Bạn đã đặt giá thành công!");

        } catch (IllegalArgumentException e) {
            // Hứng những câu từ hàm placeBid (VD: "Giá thầu phải từ...")
            session.sendMessage("ERROR|" + e.getMessage());

        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi hệ thống khi đặt giá.");
        }
    }
}