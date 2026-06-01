package controller;

import database.ItemDAOImpl;
import database.SessionDAO;
import function.ItemStatus;
import function.SessionChecker;
import function.SessionStatus;
import function.SystemLogger;
import models.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuctionManager {
    //Thuộc tính
    private static AuctionManager instance; //Singleton
    private CopyOnWriteArrayList<AuctionSession> finishedSessions = new CopyOnWriteArrayList<>();
    private SessionChecker sessionChecker = new SessionChecker();
    private SessionDAO sessionDAO = new SessionDAO();
    private ItemDAOImpl itemDAO;
    private SystemLogger log = SystemLogger.getInstance();
    private PaymentManager paymentManager = PaymentManager.getInstance();
    //Contructor
    private AuctionManager(){}

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

                sessionDAO.save(session);
                itemDAO.update(item);
                log.info("Đã tạo phiên đấu giá ID:" + session.getID().toString() + "|SUCCESS");
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

                sessionDAO.save(session);
                itemDAO.update(item);
                log.info("Đã tạo phiên đấu giá ID:" + session.getID().toString() + "|SUCCESS");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public AuctionSession getSession(UUID ID) throws SQLException {
        AuctionSession re = sessionDAO.get(ID);

        if (re == null) {
            throw new RuntimeException("Không tìm thấy phiên đấu giá");
        } else {
            return re;
        }
    }

    public List<AuctionSession> getSessionsAll() throws SQLException{
        List<AuctionSession> list = sessionDAO.getAll();
        if(list.size() == 0){
            throw new NullPointerException("Không tồn tại phiên đấu giá");
        }
        return list;
    }



    public void placeBid(AuctionSession auctionSession, Bidder bidder, Double amount) throws IllegalArgumentException,NullPointerException, SQLException {
        if(auctionSession == null || bidder == null){
            throw new NullPointerException("Null tham số");
        }

        //Logic kiểm tra phiên có trong db chưa?
        //Cơ chế extend thời gian

        auctionSession.placeBid(bidder, amount);
        sessionDAO.update(auctionSession);
        log.info("Đặt giá thầu thành công: " + amount);

    }

    public void finishSession(AuctionSession session) throws Exception,NullPointerException,IllegalArgumentException{
        if(session == null){
            throw new NullPointerException("Null session");
        }
        //Logic kiểm tra phiên có trong db chưa?
        HashMap<String,Object> sessionMap = new HashMap<>();
        sessionMap.put("session",session);
        Transaction settlementTransaction = paymentManager.createTransaction(SettlementTransaction.class,sessionMap);

        session.finishSession();
        sessionDAO.update(session);
        paymentManager.executeTransaction(settlementTransaction);
        itemDAO.update(session.getItem());   //Chưa tin nó lưu được vào item table
        log.info("Phiên đấu giá ID:" + session.getID().toString() + " đã kết thúc");

    }


    public void cancelSession(AuctionSession session) throws NullPointerException,IllegalArgumentException, SQLException {
        /*
         * Điều kiện hủy phiên : Phiên đang ở trạng thái hoặc UPCOMING hoặc RUNNING hoặc PENDING
         */
        if(session == null){
            throw new NullPointerException("Null session");
        }
        //Logic kiểm tra phiên có trong db chưa?

        session.cancelSession();
        sessionDAO.update(session);
        log.info("Phiên đấu giá ID:" + session.getID().toString() + " đã bị hủy");


    }

    public void pendSession(AuctionSession session) throws NullPointerException,IllegalArgumentException, SQLException{
        if(session == null){
            throw new NullPointerException("Null session");
        }
        //Logic kiểm tra phiên có trong db chưa?

        session.pendSession();
        sessionDAO.update(session);
        log.info("Phiên đấu giá ID:" + session.getID().toString() + " đã bị dừng");



    }

    public void unPendSession(AuctionSession session,LocalDateTime newEndTime) throws NullPointerException, SQLException {
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
        //Logic kiểm tra phiên có trong db chưa?

            session.unPendSession();
            session.extendEndTime(newEndTime);
            sessionDAO.update(session);
    }

    public List<AuctionSession> getSessionActive() throws SQLException{
        List<AuctionSession> list = sessionDAO.getActiveSessions();
        if (list == null){
            throw new NullPointerException("Không còn phiên đang hoạt động");
        }
        return list;
    }

}
