package server.command;

import controller.brain.WalletManager;
import models.Wallet;
import server.ClientSession;
import server.Role;

import java.util.Set;
import java.util.UUID;

public class WithdrawCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.BIDDER, Role.SELLER);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp mới: WITHDRAW | số_tiền
        if (args.length < 2) {
            session.sendMessage("ERROR|Thiếu thông tin rút tiền. Cú pháp: WITHDRAW|Số_tiền");
            return;
        }

        try {
            long amount = Long.parseLong(args[1]);
            UUID ownerID = session.getCurrentUser().getID();

            if (amount <= 0) {
                session.sendMessage("ERROR|Số tiền rút phải lớn hơn 0.");
                return;
            }

            // Tự động tìm ID ví
            Wallet wallet = WalletManager.getInstance().getWallet(ownerID);

            if (wallet == null) {
                session.sendMessage("ERROR|Tài khoản của bạn chưa được khởi tạo ví!");
                return;
            }
            UUID walletID=wallet.getID();

            // Gọi hàm rút
            WalletManager.getInstance().withdrawWallet(walletID, ownerID, amount);

            session.sendMessage("SUCCESS_WITHDRAW|Yêu cầu rút " + amount + " VNĐ đã được xử lý thành công!");

        } catch (IllegalArgumentException e) {
            // Hứng cái lỗi "Số dư không đủ" từ hàm getWalletHelper hoặc withdraw
            session.sendMessage("ERROR|" + e.getMessage());
        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi hệ thống khi xử lý rút tiền.");
            e.printStackTrace();
        }
    }
}
