package myWeb.Model;

public abstract class User {
    private String ID, Name, Password;

    //Getter
    protected String getName(){return Name;}
    protected String getID(){return ID;}
    //Setter
    protected void setName(String name){
        Name = name;
    }

}
