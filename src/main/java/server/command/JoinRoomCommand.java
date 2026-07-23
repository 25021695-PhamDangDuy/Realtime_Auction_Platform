package server.command;

import service.brain.AuctionManager;
import server.ClientSession;
import models.AuctionSession;
import server.Role;
// import controller.SessionController;

import java.util.Set;
import java.util.UUID;

public class JoinRoomCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.BIDDER, Role.SELLER, Role.ADMIN);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        //Cú pháp chuẩn JOIN_ROOM| roomId
        try {
            UUID roomId = UUID.fromString(args[1]);

            // 1. Tìm phòng trong Database/Memory
            AuctionSession targetRoom = AuctionManager.getInstance().getSession(roomId);

            if (targetRoom != null) {
                // 2. PHÉP THUẬT NẰM Ở ĐÂY: Cho ông khách này ngồi vào ghế quan sát của phòng!
                // Vì ClientSession giờ đã implement AuctionObserver, ta có thể truyền nó vào hàm attach
                targetRoom.attach(session);

                session.sendMessage("SUCCESS_JOIN_ROOM|Bạn đã vào phòng thành công.");
                System.out.println(session.getCurrentUser().getName() + " đã vào phòng " + roomId);
            } else {
                session.sendMessage("ERROR|Phòng không tồn tại|");
            }
        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi khi vào phòng|" + e.getMessage());
        }
    }
}