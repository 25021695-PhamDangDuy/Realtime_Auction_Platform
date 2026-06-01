package models;

import server.Role;

import java.util.UUID;

public class Admin extends User{
    public Admin(String name, String pw){super(name,pw);}
    public Admin(UUID id, String name, String pw){super(id,name,pw);}

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}
