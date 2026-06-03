package models;

import server.Role;

import java.util.UUID;

public abstract class User {
    private UUID ID;
    private String  Name, Password;
    private Role role;
    private Wallet wallet;

    public User(String Name,String Password) {
        this.ID = UUID.randomUUID();
        this.Name = Name;
        this.Password = Password;
    }
    public User(UUID ID, String name, String pw){
        this.ID = ID;
        this.Name = name;
        this.Password = pw;
    }

    //Getter
    public String getName(){return Name;}
    public UUID getID(){return ID;}
    public String getPassword(){return Password;}
    public abstract Role getRole();
    //Setter
    protected void setName(String name) {
        Name = name;
    }
    public void setPassword(String newPW){Password = newPW; }
    protected void setRole(Role role){this.role = role;}
    public void setWallet(Wallet wallet){this.wallet = wallet;}
    public Wallet getWallet(){return wallet;}
}


