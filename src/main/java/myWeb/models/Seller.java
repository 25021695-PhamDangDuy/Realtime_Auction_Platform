package myWeb.models;

import myWeb.controller.AuctionObserver;

import java.util.List;

public class Seller extends Bidder implements AuctionObserver {
    List<Item> productlist;
    double balance;

    public Seller(String ID,String Name,String password) {
        super(ID, Name, password);
        this.balance = 0.0;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void update(String message){
        System.out.println(" Seller " + getName() + " : " + message);
    }

}
