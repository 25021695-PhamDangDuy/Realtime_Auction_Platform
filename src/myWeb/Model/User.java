package myWeb.Model;

import java.util.List;
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
class Seller extends User{
    List<Item> productlist;
    double balance;
    public Seller(String ID,String Name,String password) {
        super(ID, Name, password);
        this.balance = 0.0;
    }
    void setBalance(double bal){
        balance += bal;
    }
}


