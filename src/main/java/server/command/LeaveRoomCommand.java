package server.command;

import function.SystemLogger;
import service.brain.AuctionManager;
import server.ClientSession;
import models.AuctionSession;
import server.Role;
// import controller.SessionController;

import java.util.Set;
import java.util.UUID;

public class LeaveRoomCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.BIDDER, Role.SELLER, Role.ADMIN);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        //Cú pháp chuẩn : LEAVE_ROOM| rômId
        try {
            UUID roomId = UUID.fromString(args[1]);

            SystemLogger.getInstance().warning("LeaveCM process 1");

            // 1. Tìm phòng trong Database/Memory
            AuctionSession targetRoom = AuctionManager.getInstance().getSession(roomId);

            SystemLogger.getInstance().warning("LeaveCM process 2");
            if (targetRoom != null) {
                // 2. PHÉP THUẬT NẰM Ở ĐÂY: Cho ông khách này ngồi vào ghế quan sát của phòng!
                // Vì ClientSession giờ đã implement AuctionObserver, ta có thể truyền nó vào hàm detach
                targetRoom.detach(session);

                SystemLogger.getInstance().warning("LeaveCM process 3");
                session.sendMessage("SUCCESS_LEAVE_ROOM|Bạn đã rời phòng thành công.");
                System.out.println(session.getCurrentUser().getName() + " đã rời phòng " + roomId);
            } else {
                session.sendMessage("ERROR|Phòng không tồn tại.");
            }
        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi khi rời phòng|" + e.getMessage() + "|");
        }
    }
}