package myWeb.Controller;

import myWeb.Model.User;

import java.util.ArrayList;
import java.util.List;

public class AuctionManager {
    private static AuctionManager instance;
    private List<User> users;
    private AuctionManager(){
        users = new ArrayList<>();
    }
    public static AuctionManager getInstance(){
        if (instance == null){
            instance = new AuctionManager();
        }
        return instance;
    }
}
