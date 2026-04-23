package myWeb.controller;

import myWeb.models.User;
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
    public void createSession(String productId, double reservePrice){
        AuctionSession session = new AuctionSession(productId,reservePrice);
        sessions.add(session);
        System.out.println("Đã tạo phiên đấu giá cho:" + productId);
    }
    public synchronized void handleBid(String productId, String bidder, double amount){
        for (AuctionSession s : sessions){
            if (s.getProduceId().equals(productId)){
                s.placeBid(bidder,amount);
                break;
            }
        }
    }



}
