package myWeb.models;

public abstract class User {
    private String ID, Name, Password;

    public User(String ID,String Name,String Password) {
        this.ID = ID;
        this.Name = Name;
        this.Password = Password;
    }
    //Getter
    protected String getName(){return Name;}
    protected String getID(){return ID;}
    //Setter
    protected void setName(String name) {
        Name = name;
    }


}


