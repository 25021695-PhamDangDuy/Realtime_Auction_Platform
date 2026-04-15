package myWeb.Controller;

import myWeb.Model.User;
import java.util.ArrayList;
import java.util.List;

public class AuctionManager {
    //Thuộc tính
    private static AuctionManager instance; //Singleton
    private List<User> users;

    //Contructor
    private AuctionManager(){
        users = new ArrayList<>();
    }
    public static AuctionManager getInstance() {
        //Thêm khóa an toàn cho safety mutiThreads: synchronized
        synchronized (AuctionManager.class) {
            if (instance == null) {
                instance = new AuctionManager();
            }
            return instance;
        }
    }


}
