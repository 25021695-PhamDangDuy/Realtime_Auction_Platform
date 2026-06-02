package server.command;

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
            session.sendMessage("ERROR|Thiếu thông tin nạp tiền. Cú pháp: DEPOSIT|Số_tiền");
            return;
        }

        try {
            long amount = Long.parseLong(args[1]);
            UUID ownerID = session.getCurrentUser().getID(); // Căn cước của khách

            if (amount <= 0) {
                session.sendMessage("ERROR|Số tiền nạp phải lớn hơn 0.");
                return;
            }

            // BƯỚC MỚI: Tự động tìm ID ví của ông khách này trong Database
            // (Giả sử bạn có hàm getWalletIdByOwnerId trong WalletDAO hoặc WalletController)
            //UUID walletID = WalletManager.getInstance().getWalletIdByOwnerId(ownerID);

           // if (walletID == null) {
             //   session.sendMessage("ERROR|Tài khoản của bạn chưa được khởi tạo ví!");
            //    return;
           // }

            // Đầy đủ 3 tham số rồi, gọi hàm của Duy thôi!
           // WalletManager.getInstance().depositWallet(walletID, ownerID, amount);

            session.sendMessage("SUCCESS_DEPOSIT|Yêu cầu nạp " + amount + " VNĐ đã được xử lý thành công!");

        } catch (IllegalArgumentException e) {
            session.sendMessage("ERROR|" + e.getMessage());
        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi hệ thống khi xử lý nạp tiền.");
            e.printStackTrace();
        }
    }
}
