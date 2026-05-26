package myWeb.server;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {
    private static final Set<ClientHandler> activeClients = ConcurrentHashMap.newKeySet();

    public static void addClient(ClientHandler client) { activeClients.add(client); }

    public static void removeClient(ClientHandler client) { activeClients.remove(client); }

    public static void broadcastMessage(String message) {
        for (ClientHandler client : activeClients) client.sendMessage(message);
    }

    public static void sendMessageToUser(String targetUserId, String message) {
        for (ClientHandler client : activeClients) {
            String id = client.getUserId();
            if (id != null && id.equals(targetUserId)) {
                client.sendMessage(message);
                break;
            }
        }
    }

    public static void sendMessageToGroup(Set<String> targetUserIds, String message) {
        for (ClientHandler client : activeClients) {
            String id = client.getUserId();
            if (id != null && targetUserIds.contains(id)) {
                client.sendMessage(message);
            }
        }
    }
}
