package server.command;


import service.brain.WalletManager;
import models.Wallet;
import server.ClientSession;
import server.Role;

import java.util.Set;
import java.util.UUID;

public class DepositCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.BIDDER, Role.SELLER);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp mới: DEPOSIT | số_tiền
        if (args.length < 2) {
            session.sendMessage("ERROR|Thiếu thông tin nạp tiền. ");
            return;
        }

        try {
            long amount = Long.parseLong(args[1]);
            String ownerName = session.getCurrentUser().getName(); // Căn cước của khách
            UUID ownerID=session.getCurrentUser().getID();
            if (amount <= 0) {
                session.sendMessage("ERROR|Số tiền nạp phải lớn hơn 0.");
                return;
            }

            //BƯỚC MỚI: Tự động tìm ID ví của ông khách này trong Database
            Wallet wallet = WalletManager.getInstance().getWalletbyOwner(ownerName);

            if (wallet == null) {
               session.sendMessage("ERROR|Tài khoản của bạn chưa được khởi tạo ví!");
                return;
            }
            UUID walletID= wallet.getID();

            // Đầy đủ 3 tham số rồi, gọi hàm
           WalletManager.getInstance().depositWallet(walletID, ownerID, amount);

            session.sendMessage("SUCCESS_DEPOSIT|Yêu cầu nạp " + amount + " VNĐ đã được xử lý thành công!");

        } catch (IllegalArgumentException e) {
            session.sendMessage("ERROR|" + e.getMessage());
        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi hệ thống khi xử lý nạp tiền.");
            e.printStackTrace();
        }
    }
}
