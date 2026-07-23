package Application;

import controller.network.MessageListener;
import controller.network.ServerConnection;
import models.User;
import server.GsonUtil;

import java.util.ArrayList;
import java.util.List;

public class TestRuntime implements MessageListener {
    private final ServerConnection connection = new ServerConnection();

    public static void main(String[] args) {
        String command = "SUCCESS_INFORMATION|{\"ID\":\"f6af4ec0-6241-4ce1-8c3f-e618b52e8ff1\",\"Name\":\"DUYPHAM\",\"Password\":\"07092007Isi\",\"wallet\":{\"ID\":\"1f1fb062-0ce7-4db0-973a-d608c43e5139\",\"ownerID\":\"f6af4ec0-6241-4ce1-8c3f-e618b52e8ff1\",\"balance\":1000,\"balanceLocked\":0,\"withdrawKey\":{},\"depositKey\":{},\"lockMoneyKey\":{},\"unlockMoneyKey\":{}}}";
        String[] parts = command.split("\\|");
        String target = parts[1];

        System.out.println(target);
        User user = GsonUtil.gson.fromJson(target, User.class);
    }

    @Override
    public void onMessageReceived(String message) {

    }
}
