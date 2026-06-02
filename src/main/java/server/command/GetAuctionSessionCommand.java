package server.command;

import controller.brain.AuctionManager;
import models.AuctionSession;
import server.ClientSession;
import server.Role;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class GetAuctionSessionCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.BIDDER,Role.SELLER,Role.ADMIN);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp chuẩn Client gửi lên: GET_SESSIONS | TRẠNG_THÁI
        // (Trạng thái gồm: ACTIVE, UPCOMING)
        if (args.length < 2) {
            session.sendMessage("ERROR|Thiếu trạng thái phiên đấu giá cần lấy.");
            return;
        }

        try {
            String status = args[1].toUpperCase();
            List<AuctionSession> sessionList = null;

            // ========================================================
            // ĐỊNH TUYẾN THEO TRẠNG THÁI (Giống hệt lúc làm Item)
            // ========================================================
            switch (status) {
                case "ACTIVE":
                    sessionList = AuctionManager.getInstance().getSessionActive();
                    break;
                case "UPCOMING":
                    sessionList = AuctionManager.getInstance().getSessionUpcoming();
                    break;
                default:
                    session.sendMessage("ERROR|Trạng thái phiên đấu giá không hợp lệ: " + status);
                    return;
            }

            // Nếu qua được ải trên mà list vẫn rỗng (đề phòng thêm)
            if (sessionList == null || sessionList.isEmpty()) {
                session.sendMessage("SUCCESS_SESSIONS|EMPTY");
                return;
            }

            // ========================================================
            // GÓI GHÉM DỮ LIỆU ĐỂ GỬI VỀ GIAO DIỆN SẢNH
            // ========================================================
            StringBuilder response = new StringBuilder("SUCCESS_SESSIONS");

            /*
             Mở comment và sửa lại tên các hàm get() cho đúng với class AuctionSession của bạn.
             Quy ước ở đây là: sessionID, tên_món_hàng, giá_hiện_tại

            for (AuctionSession aucSession : sessionList) {
                response.append("|")
                        .append(aucSession.getSessionID()).append(",")
                        .append(aucSession.getItem().getName()).append(",")
                        .append(aucSession.getCurrentPrice());
            }
            */

            session.sendMessage(response.toString());
            System.out.println("[Command] Đã gửi danh sách phòng " + status + " cho Bidder: " + session.getCurrentUser().getName());

        } catch (NullPointerException e) {
            // Bắt đúng cái lỗi mà file Controller của bạn Duy ném ra khi không có phòng
            System.out.println("[Info] " + e.getMessage());
            session.sendMessage("SUCCESS_SESSIONS|EMPTY");

        } catch (SQLException e) {
            session.sendMessage("ERROR|Lỗi truy xuất cơ sở dữ liệu khi tải danh sách phòng.");
            System.err.println("[SQL_ERROR] Lỗi GetAuctionSession: " + e.getMessage());
        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi hệ thống không xác định.");
            e.printStackTrace();
        }
    }
}