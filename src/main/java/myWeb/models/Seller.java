package myWeb.models;

import java.util.List;

public class Seller extends Bidder{
    List<Item> productlist;
    double balance;
    public Seller(String ID,String Name,String password) {
        super(ID, Name, password);
        this.balance = 0.0;
    }
    public void setBalance(double bal){
        balance += bal;
    }
}
