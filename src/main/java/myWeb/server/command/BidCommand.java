package myWeb.server.command;

import myWeb.server.ClientManager;
import myWeb.server.ClientSession;
import myWeb.server.Role;
import myWeb.controller.AuctionManager;
import myWeb.models.AuctionSession;
import myWeb.models.Bidder;

import java.util.Set;

public class BidCommand implements Command {
    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.BIDDER, Role.SELLER);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp từ Client: BID|ProductId|SốTiền
        if (args.length < 3) {
            session.sendMessage("ERROR|Thiếu tham số đặt giá.");
            return;
        }

        String productId = args[1];
        try {
            Double amount = Double.parseDouble(args[2]);
            AuctionManager manager = AuctionManager.getInstance();
            AuctionSession room = manager.getSession(productId);

            // Ép kiểu User trong Session thành Bidder (Vì Role đã chặn GUEST rồi nên an toàn)
            Bidder bidder = (Bidder) session.getCurrentUser();

            // Gọi Core Logic của nhóm bạn
            manager.placeBid(room, bidder, amount);

            // Báo thành công cho người đặt
            session.sendMessage("SUCCESS|Đã đặt giá " + amount + " cho " + productId);

            // Lưu ý: Vì Bidder của bạn có hàm update() chỉ in ra System.out,
            // nên ta vẫn dùng ClientManager để bắn thông báo về qua mạng Socket.
            String alertMsg = "ROOM_ALERT|" + productId + "|" + amount;
            ClientManager.broadcastMessage(alertMsg); // Phát thanh toàn server

        } catch (NumberFormatException e) {
            session.sendMessage("ERROR|Số tiền không hợp lệ.");
        } catch (NullPointerException | IllegalArgumentException e) {
            // Bắt trọn các lỗi nghiệp vụ do Core Logic của bạn ném ra (VD: "Không tìm thấy session", "Người bán không thể tự đấu giá")
            session.sendMessage("ERROR|" + e.getMessage());
        }
    }
}