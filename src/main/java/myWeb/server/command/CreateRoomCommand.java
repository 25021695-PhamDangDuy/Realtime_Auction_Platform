package myWeb.server.command;

import myWeb.server.ClientManager;
import myWeb.server.ClientSession;
import myWeb.server.Role;
import myWeb.controller.AuctionManager;
import myWeb.models.Item;
import myWeb.models.Seller;

import java.time.LocalDateTime;
import java.util.Set;

public class CreateRoomCommand implements Command {
    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.SELLER);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp: CREATE_ROOM|ProductId|ItemName|Condition|StartPrice|MinIncrement|SốPhútĐấuGiá
        try {
            String productId = args[1];
            String itemName = args[2];
            String condition = args[3];
            double startPrice = Double.parseDouble(args[4]);
            double minIncrement = Double.parseDouble(args[5]);
            int minutes = Integer.parseInt(args[6]);

            Seller seller = (Seller) session.getCurrentUser();

            // Khởi tạo Item theo constructor nhóm bạn viết
            Item item = new Item(seller, productId + "_ITEM", itemName, condition, startPrice) {
                // Do Item là Abstract Class (theo ảnh), bạn cần có 1 class con thực tế (VD: Art, Electronics)
                // Hoặc khởi tạo Anonymous class tạm thời ở đây.
            };

            LocalDateTime endTime = LocalDateTime.now().plusMinutes(minutes);

            // Gọi Core Logic
            AuctionManager.getInstance().createSession(
                    productId, item, seller, startPrice, minIncrement, endTime
            );

            session.sendMessage("SUCCESS|Tạo phòng đấu giá thành công!");
            ClientManager.broadcastMessage("SERVER_MSG|Có phiên đấu giá mới: " + itemName);

        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi tạo phòng: " + e.getMessage());
        }
    }
}