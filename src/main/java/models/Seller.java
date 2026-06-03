package models;

import controller.AuctionObserver;
import server.Role;

import java.util.List;
import java.util.UUID;

public class Seller extends Bidder implements AuctionObserver {
    List<Item> productlist;


    public Seller(String Name,String password) {
        super(Name, password);
    }
    public Seller(UUID ID, String name, String pw, Wallet wallet){
        super(ID, name, pw, wallet);
    }

    @Override
    public String getName() {
        return super.getName();
    }
    public Role getRole(){return Role.SELLER;}

    @Override
    public void update(String message){
        System.out.println(" Seller " + getName() + " : " + message);
    }

}
