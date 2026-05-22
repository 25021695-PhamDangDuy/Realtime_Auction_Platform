package myWeb.models;
import myWeb.controller.AuctionObserver;
public class Bidder extends User implements AuctionObserver {
    private double balance=0;

    public Bidder(String id,String name,String password){
        super(id,name,password);
    }

    public void setBalance(double bal){
        if (bal < 0){
            System.out.println("số dư không hợp lệ");
        }else {
            balance = bal;
        }
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void update(String message){
        System.out.println("Bidder " + getName() + " : " + message);

    }

}

