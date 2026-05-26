package myWeb.server.command;

import myWeb.server.ClientSession;
import myWeb.server.Role;
import java.util.Set;

public interface Command {
    Set<Role> getAllowedRoles();
    void execute(ClientSession session, String[] args);
}
