package myWeb.Models;
import myWeb.Function.balanceSkills.*;

public class Bidder extends User implements balanceModifyAble{
    private double balance=0;

    public Bidder(String id,String name,String password){
        super(id,name,password);
    }

    @Override
    public void setBalance(double bal){
        balance+=bal;
    }

    @Override
    public double getBalance() {
        return balance;
    }
}

