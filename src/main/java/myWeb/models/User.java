package myWeb.models;

public abstract class User {
    private String ID, Name, Password;

    public User(String ID,String Name,String Password) {
        this.ID = ID;
        this.Name = Name;
        this.Password = Password;
    }
    //Getter
    public String getName(){return Name;}
    public String getID(){return ID;}
    public String getPassword(){return Password;}
    //Setter
    protected void setName(String name) {
        Name = name;
    }
    public void setPassword(String newPW){Password = newPW; }


}


