package myWeb.controller;

import myWeb.function.ItemStatus;
import myWeb.function.SessionChecker;
import myWeb.function.SessionStatus;
import myWeb.models.Bidder;
import myWeb.models.Item;
import myWeb.models.Seller;
import myWeb.models.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuctionManager {
    //Thuộc tính
    private static AuctionManager instance; //Singleton
    private List<User> users;
    private CopyOnWriteArrayList<AuctionSession> sessions = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<AuctionSession> finishedSessions = new CopyOnWriteArrayList<>();
    private SessionChecker sessionChecker = new SessionChecker();

    //Contructor
    private AuctionManager() {
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
    //CreateSession: Quy tắc là đặt sản phẩm đấu giá phải có tổng thời gian tối thiểu 30 phút, và hạn đóng phải trước 20p so với thời gian mở.
    //CreateSession(1): Sử dụng khi muốn tạo phiên đấu giá với trạng thái mở ngay lập tức
    public void createSession(String productId, Item item, Seller seller, double startPrice, double minIncrement, LocalDateTime endtime) {
        try{
            LocalDateTime now = LocalDateTime.now();
            if(sessionChecker.durationTime(now,endtime,30,43200) && sessionChecker.isItemAvailable(item)){
                AuctionSession session = new AuctionSession(productId,item,seller,startPrice,minIncrement,endtime,now,SessionStatus.RUNNING);
                item.setItemStatus(ItemStatus.AUCTIONING);
                sessions.add(session);
                System.out.println("Đã tạo phiên đấu giá cho:" + productId);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void createSession(String productId, Item item, Seller seller, double startPrice, double minIncrement, LocalDateTime endtime, LocalDateTime startTime) {
        try{
            if(sessionChecker.durationTime(startTime,endtime,30,43200) && sessionChecker.isItemAvailable(item)){
                AuctionSession session = new AuctionSession(productId,item,seller,startPrice,minIncrement,endtime,startTime,SessionStatus.UPCOMING);
                item.setItemStatus(ItemStatus.AUCTIONING);
                sessions.add(session);
                System.out.println("Đã tạo phiên đấu giá cho:" + productId);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public AuctionSession getSession(String ID) {
        AuctionSession re = null;
        for (AuctionSession as : sessions) {
            if (ID.equals(as.getProduceId())) {
                re = as;
            }
        }
        if (re == null) {
            throw new RuntimeException("Không tìm thấy phiên đấu giá");
        } else {
            return re;
        }
    }

    public List<AuctionSession> getSessions() {
        return sessions;
    }

    public void placeBid(AuctionSession auctionSession, Bidder bidder, Double amount) throws IllegalArgumentException,NullPointerException {
        if(auctionSession == null || bidder == null || amount == null){
            throw new NullPointerException("Null tham số");
        }
        if(!sessions.contains(auctionSession)){
            throw new IllegalArgumentException("Không tìm thấy session");
        }
        auctionSession.placeBid(bidder, amount);
        System.out.println("Đặt giá thầu thành công: " + amount);

    }

    public void finishSession(AuctionSession session) throws Exception,NullPointerException,IllegalArgumentException{
        if(session == null){
            throw new NullPointerException("Null session");
        }
        if(!sessions.contains(session)){
            throw new IllegalArgumentException("Không tìm thấy session");
        }
        if(sessionChecker.isSessionTimeUp(session)){
            session.setStatus(SessionStatus.FINISHED);
            Bidder winner = session.getTopBidder();
            Item reward = session.getItem();

            winner.addItem(reward);
            reward.setOwner(winner);
            reward.setItemStatus(ItemStatus.SOLD);
        }else {
            throw new Exception("Session still Auctioning");
        }
    }


    public void cancelSession(AuctionSession session) throws NullPointerException,IllegalArgumentException {
        /**
         * Điều kiện hủy phiên : Phiên đang ở trạng thái hoặc UPCOMING hoặc RUNNING hoặc PENDING
         */
        if(session == null){
            throw new NullPointerException("Null session");
        }
        if(!sessions.contains(session)){
            throw new IllegalArgumentException("Không tìm thấy session");
        }
        if(session.getStatus() != SessionStatus.CANCELED){
            session.setStatus(SessionStatus.CANCELED);
            Item item = session.getItem();
            item.setItemStatus(ItemStatus.AVAILABLE);
        }
    }

    public void pendSession(AuctionSession session) throws NullPointerException,IllegalArgumentException{
        if(session == null){
            throw new NullPointerException("Null session");
        }
        if(!sessions.contains(session)){
            throw new IllegalArgumentException("Không tìm thấy session");
        }
        /*
        Điều kiện dùng phiên là: phiên đang chạy
         */
        if(sessionChecker.isAuctioning(session) && !sessionChecker.isSessionTimeUp(session)){
            session.setStatus(SessionStatus.PENDING);
        }
    }

}
