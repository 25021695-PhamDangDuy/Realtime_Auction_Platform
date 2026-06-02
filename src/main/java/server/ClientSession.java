package server;

import java.io.PrintWriter;

import controller.AuctionObserver;
import models.User;
import models.Wallet;

import java.util.UUID;

public class ClientSession implements AuctionObserver {
    private final PrintWriter out;
    private final ClientHandler handler;
    private User currentUser;
    private Role role;

    public ClientSession(PrintWriter out, ClientHandler handler) {
        this.out = out;
        this.handler = handler;
        this.currentUser = null;
        this.role = Role.GUEST;
    }
    @Override
    public void update(String message) {
        // Khi phòng đấu giá có biến, gọi hàm này.
        // Ta chỉ việc lấy tin nhắn đó ném thẳng vào ống mạng trả về cho App!
        this.sendMessage(message);
    }

    public void sendMessage(String message) {
        if (out != null) out.println(message);
    }

    public UUID getUserId() {
        return (this.currentUser != null) ? this.currentUser.getID() : null;
    }

    public ClientHandler getHandler() { return handler; }
    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
