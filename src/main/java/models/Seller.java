package models;

import service.AuctionObserver;
import server.Role;

import java.util.List;
import java.util.UUID;

public class Seller extends Bidder implements AuctionObserver {
    transient List<Item> productlist;


    public Seller(String Name,String password) {
        super(Name, password);
    }
    public Seller(UUID ID, String name, String pw){
        super(ID, name, pw);
        this.setRole(Role.SELLER);
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
