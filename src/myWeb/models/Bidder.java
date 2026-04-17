package myWeb.models;
import myWeb.function.balanceSkills.*;

public class Bidder extends User implements balanceModifiable{
    private double balance=0;

    public Bidder(String id,String name,String password){
        super(id,name,password);
    }

    @Override
    public void setBalance(double bal){
        if (bal < 0){
            System.out.println("số dư không hợp lệ");
        }else {
            balance = bal;
        }
    }

    @Override
    public double getBalance() {
        return balance;
    }

}

