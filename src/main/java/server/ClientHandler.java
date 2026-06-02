package server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import server.command.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ClientSession clientSession;

    private static final Map<String, Command> commandRegistry = new HashMap<>();

    static {
        commandRegistry.put("LOGIN", new LoginCommand());
        commandRegistry.put("BID", new BidCommand());
        commandRegistry.put("REGISTER",new RegisterCommand());
        commandRegistry.put("DEPOSIT",new DepositCommand());
        commandRegistry.put("WITHDRAW",new WithdrawCommand());
        commandRegistry.put("CREAT_ITEM",new CreateItemCommand());
        commandRegistry.put("LOGOUT",new LogoutCommand());
        commandRegistry.put("GetAuctionSession", new GetAuctionSessionCommand());

    }

    public ClientHandler(Socket socket) { this.socket = socket; }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            this.clientSession = new ClientSession(out, this);

            sendMessage("SERVER_MSG|Kết nối thành công!");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.trim().isEmpty()) continue;

                String[] parts = message.split("\\|");
                Command command = commandRegistry.get(parts[0].toUpperCase().trim());

                if (command == null) {
                    sendMessage("ERROR|Lệnh không hợp lệ.");
                    continue;
                }

                if (!command.getAllowedRoles().contains(clientSession.getRole())) {
                    sendMessage("ERROR|Từ chối truy cập do không đủ quyền.");
                    continue;
                }

                command.execute(clientSession, parts);
            }
        } catch (IOException ignored) {
        } finally {
            ClientManager.removeClient(this);
            closeResources();
        }
    }

    public void sendMessage(String message) { if (out != null) out.println(message); }
    public UUID getUserId() { return (clientSession != null) ? clientSession.getUserId() : null; }

    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }
}
