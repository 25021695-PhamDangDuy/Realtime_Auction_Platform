package myWeb.models;

import java.util.UUID;

public abstract class User {
    private UUID ID;
    private String  Name, Password;

    public User(String Name,String Password) {
        this.ID = UUID.randomUUID();
        this.Name = Name;
        this.Password = Password;
    }
    public User(UUID ID, String name, String pw){
        this.ID = ID;
        this.Name = Name;
        this.Password = Password;
    }
    //Getter
    public String getName(){return Name;}
    public UUID getID(){return ID;}
    public String getPassword(){return Password;}
    //Setter
    protected void setName(String name) {
        Name = name;
    }
    public void setPassword(String newPW){Password = newPW; }

}


