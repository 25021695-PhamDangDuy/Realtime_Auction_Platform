package myWeb.models;

import java.util.UUID;

public class Admin extends User{
    public Admin(String name, String pw){super(name,pw);}
    public Admin(UUID id, String name, String pw){super(id,name,pw);}
}
