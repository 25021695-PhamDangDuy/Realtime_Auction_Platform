package server.command;

import controller.brain.WalletManager;
import models.Wallet;
import server.ClientSession;
import models.User;
import server.Role;

import java.util.Set;
import java.util.UUID;

public class ViewWalletCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        // Cả người mua và người bán đều có ví để kiểm tra
        return Set.of(Role.BIDDER, Role.SELLER);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp Client gửi lên cực ngắn: VIEW_WALLET

        try {
            User currentUser = session.getCurrentUser();
            if (currentUser == null) {
                session.sendMessage("ERROR|Bạn cần đăng nhập để xem ví.");
                return;
            }
            // ========================================================
            // CHỈ LẤY SỐ DƯ, KHÔNG LẤY CẢ OBJECT VÍ
            // =======================================================
            // Cách 1: Nếu User có sẵn hàm lấy ví
            long currentBalance = WalletManager.getInstance().getBalancebyOwnerID(currentUser.getName());
            long lockBalance=WalletManager.getInstance().getBalanceLockedbyOwnerID(currentUser.getName());



            // Gửi duy nhất con số về cho an toàn tuyệt đối
            session.sendMessage("SUCCESS_WALLET_BALANCE|" + currentBalance+"|"+lockBalance);

            System.out.println("[Command] Đã gửi số dư cho: " + currentUser.getName());

        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi hệ thống khi kiểm tra số dư ví.");
            e.printStackTrace();
        }
    }
}