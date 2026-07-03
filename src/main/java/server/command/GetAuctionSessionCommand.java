package server.command;

import service.brain.AuctionManager;
import models.AuctionSession;
import server.ClientSession;
import server.GsonUtil;
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

            // GÓI GHÉM BẰNG GSON (Thay thế hoàn toàn StringBuilder)
            // 1. Ép cả cái List thành 1 chuỗi JSON dài
            String jsonList = GsonUtil.gson.toJson(sessionList);

            // 2. Nối thêm cái mã lệnh ở đầu để Lễ tân Client biết đường nhận
            session.sendMessage("SUCCESS_SESSIONS|" + jsonList);

            System.out.println("[Command] Đã gửi danh sách Session bằng GSON!");

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