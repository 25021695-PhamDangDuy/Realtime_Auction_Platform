package myWeb.server.command;

import myWeb.server.ClientSession;
import myWeb.server.Role;
import myWeb.controller.AuctionManager;
import myWeb.models.AuctionSession;

import java.util.List;
import java.util.Set;

public class GetRoomsCommand implements Command {
    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.BIDDER, Role.SELLER, Role.ADMIN, Role.GUEST); // Cho cả khách xem
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        List<AuctionSession> sessions = AuctionManager.getInstance().getSessions();

        if (sessions == null || sessions.isEmpty()) {
            session.sendMessage("ROOM_LIST|EMPTY");
            return;
        }

        StringBuilder sb = new StringBuilder("ROOM_LIST|");
        for (AuctionSession s : sessions) {
            // Đóng gói: ProductID, Tên Item, Giá hiện tại
            sb.append(s.getProduceId()).append(",")
                    .append(s.getItem().getName()).append(",")
                    .append(s.getCurrentPrice()).append("|");
        }

        session.sendMessage(sb.toString());
    }
}