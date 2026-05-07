package myWeb.controller;

import myWeb.models.Item;
import myWeb.models.Seller;
import myWeb.models.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionManager {
    //Thuộc tính
    private static AuctionManager instance; //Singleton
    private List<User> users;
    private List<AuctionSession> sessions = new ArrayList<>();

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
    public void createSession(String produceId, Item item, Seller seller, double startPrice, double minIncreament, LocalDateTime endtime){
        AuctionSession session = new AuctionSession(produceId,item,seller,startPrice,minIncreament,endtime);
        sessions.add(session);
        System.out.println("Đã tạo phiên đấu giá cho:" + produceId);
    }

}
