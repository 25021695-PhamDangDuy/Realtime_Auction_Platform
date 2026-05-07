package myWeb.models;


public class Bidder extends User {
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

}

