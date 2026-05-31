package myWeb.controller;

import myWeb.function.ItemStatus;
import myWeb.function.SessionChecker;
import myWeb.function.SessionStatus;
import myWeb.models.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
        if(instance == null) {
            //Thêm khóa an toàn cho safety mutiThreads: synchronized
            synchronized (AuctionManager.class) {
                if (instance == null) {
                    instance = new AuctionManager();
                }
                return instance;
            }
        }
        return instance;
    }
    //CreateSession: Quy tắc là đặt sản phẩm đấu giá phải có tổng thời gian tối thiểu 30 phút, và hạn đóng phải trước 20p so với thời gian mở.
    //CreateSession(1): Sử dụng khi muốn tạo phiên đấu giá với trạng thái mở ngay lập tức
    public void createSession(Item item, Seller seller, long startPrice, long minIncrement, LocalDateTime endtime) {
        try{
            LocalDateTime now = LocalDateTime.now();
            if(sessionChecker.durationTime(now,endtime,1,43200) && sessionChecker.isItemAvailable(item)){
                AuctionSession session = new AuctionSession(item,seller,startPrice,minIncrement,endtime,now,SessionStatus.RUNNING);
                item.setItemStatus(ItemStatus.AUCTIONING);
                sessions.add(session);
                System.out.println("Đã tạo phiên đấu giá cho:" + session.getID().toString());
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void createSession(Item item, Seller seller, long startPrice, long minIncrement, LocalDateTime endtime, LocalDateTime startTime) {
        try{
            if(sessionChecker.durationTime(startTime,endtime,5,43200) && sessionChecker.isItemAvailable(item)){
                AuctionSession session = new AuctionSession(item,seller,startPrice,minIncrement,endtime,startTime,SessionStatus.UPCOMING);
                item.setItemStatus(ItemStatus.AUCTIONING);
                sessions.add(session);
                System.out.println("Đã tạo phiên đấu giá cho:" + session.getID().toString());
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public AuctionSession getSession(UUID ID) {
        AuctionSession re = null;
        for (AuctionSession as : sessions) {
            if (ID.equals(as.getID())) {
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

    public void placeBid(AuctionSession auctionSession, Bidder bidder, long amount) throws IllegalArgumentException,NullPointerException {
        if(auctionSession == null || bidder == null){
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
        try{
            session.finishSession();
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e);
        }
    }


    public void cancelSession(AuctionSession session) throws NullPointerException,IllegalArgumentException {
        /*
         * Điều kiện hủy phiên : Phiên đang ở trạng thái hoặc UPCOMING hoặc RUNNING hoặc PENDING
         */
        if(session == null){
            throw new NullPointerException("Null session");
        }
        if(!sessions.contains(session)){
            throw new IllegalArgumentException("Không tìm thấy session");
        }
        try{
            session.cancelSession();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void pendSession(AuctionSession session) throws NullPointerException,IllegalArgumentException{
        if(session == null){
            throw new NullPointerException("Null session");
        }
        if(!sessions.contains(session)){
            throw new IllegalArgumentException("Không tìm thấy session");
        }
        try{
            session.pendSession();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public void unPendSession(AuctionSession session,LocalDateTime newEndTime) {
        /*
        Khi tiến hành chạy lại(unPending) phiên thì sẽ tiến hành các điều kiện sau:
        0. Kiểm tra tham số đầu vào
        1. Mở lại trạng thái cho phiên
        2. Tăng giới hạn thời gian cho phiên
        3. Thông báo tới các Observer
         */
        if(session == null || newEndTime == null){
            throw new NullPointerException("Null tham số");
        }
        if(!sessions.contains(session)){
            throw new IllegalArgumentException("Không tìm thấy session");
        }
        try {
            session.unPendSession();
            session.extendEndTime(newEndTime);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
