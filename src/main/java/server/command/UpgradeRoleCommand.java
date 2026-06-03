package server.command;

import controller.brain.AccountController;
import database.UserDAO;
import models.User;
import server.ClientSession;
import server.GsonUtil;
import server.Role;

import java.util.Set;
import java.util.UUID;

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

            // 4. Ép GSON nén ông Seller mới này lại, nhớ bọc bằng User.class để có nhãn "type":"Seller"
            String userJson = GsonUtil.gson.toJson(upgradedUser, models.User.class);

            // 5. Gửi về cho Client
            session.sendMessage("SUCCESS_UPGRADE|" + userJson);

        } catch (Exception e) {
            session.sendMessage("ERROR|Lỗi khi nâng cấp: " + e.getMessage());
            e.printStackTrace();
        }
    }
}