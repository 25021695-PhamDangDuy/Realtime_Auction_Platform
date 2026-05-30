package server.command;

import server.ClientSession;
import server.Role;

import java.util.Set;

public interface Command {
    Set<Role> getAllowedRoles();
    void execute(ClientSession session, String[] args);
}
