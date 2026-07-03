package server.command;

import service.brain.AccountController;
import models.Seller;
import models.User;
import server.ClientSession;
import server.GsonUtil;
import server.Role;

import java.util.Set;

public class UpgradeRoleCommand implements Command {
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.BIDDER);
    }
    @Override
    public void execute(ClientSession session, String[] args) {
        try {
            models.User currentUser = session.getCurrentUser();
            String name=currentUser.getName();

            // Nếu đã là Seller rồi thì thôi
            if (currentUser instanceof models.Seller) {
                session.sendMessage("ERROR|Bạn đã là Seller rồi!");
                return;
            }
            AccountController.getInstance().upRole(currentUser);
            User upgradedUser=AccountController.getInstance().getInfor(name);

            // 3. Cập nhật lại Session trên Server
            session.setCurrentUser(upgradedUser);
            session.setRole(Role.SELLER);

            // Lấy từ Database hoặc Session
            Seller newSeller = new Seller(currentUser.getID(), currentUser.getName(),currentUser.getPassword());

            newSeller.setWallet(currentUser.getWallet());

            String json = GsonUtil.gson.toJson(newSeller, models.User.class);
            // 5. Gửi về cho Client
            session.sendMessage("SUCCESS_UPGRADE|" + json);

        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi khi nâng cấp: " + e.getMessage());
            e.printStackTrace();
        }
    }
}