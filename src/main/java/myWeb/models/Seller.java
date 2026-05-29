package myWeb.models;

import myWeb.controller.AuctionObserver;

import java.util.List;
import java.util.UUID;

public class Seller extends Bidder implements AuctionObserver {
    List<Item> productlist;


    public Seller(String Name,String password) {
        super(Name, password);
    }
    public Seller(UUID ID, String name, String pw){
        super(ID, name, pw);
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
