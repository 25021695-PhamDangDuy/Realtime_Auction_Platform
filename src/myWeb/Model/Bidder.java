package myWeb.Model;

public class Bidder extends User {
    private double balance=0;

    public Bidder(String id,String name,String password){
        super(id,name,password);
    }

    void setBalance(double bal){
        balance+=bal;
    }
}

