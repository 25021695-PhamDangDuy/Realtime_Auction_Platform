package myWeb.server;

import java.io.PrintWriter;

import myWeb.models.User;
import myWeb.server.ClientHandler;

public class ClientSession {
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

    public void sendMessage(String message) {
        if (out != null) out.println(message);
    }

    public String getUserId() {
        return (this.currentUser != null) ? this.currentUser.getID() : null;
    }

    public ClientHandler getHandler() { return handler; }
    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
