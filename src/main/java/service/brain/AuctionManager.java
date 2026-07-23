package service.brain;

import service.BidHistory;
import database.ObserverDAO;
import database.SessionDAO;
import function.ItemStatus;
import function.SessionChecker;
import function.SessionStatus;
import function.SystemLogger;
import models.*;
import database.items.getItemDao;
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
    private getItemDao itemDAO = new getItemDao();
    private SystemLogger log = SystemLogger.getInstance();
    private WalletManager walletManager = WalletManager.getInstance();
    private PaymentManager paymentManager = PaymentManager.getInstance();
    //Contructor
    private AuctionManager()  {}

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

            // DEBUG
            System.out.println("Now: " + now);
            System.out.println("EndTime: " + endtime);
            System.out.println("EndTime > Now? " + endtime.isAfter(now));
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
    public AuctionSession createSession(Item item, Seller seller, long startPrice, long minIncrement, LocalDateTime endtime, LocalDateTime startTime) {
        try{
            if(sessionChecker.durationTime(startTime,endtime,5,43200) && sessionChecker.isItemAvailable(item)){
                AuctionSession session = new AuctionSession(item,seller,startPrice,minIncrement,endtime,startTime,SessionStatus.UPCOMING);
                item.setItemStatus(ItemStatus.AUCTIONING);

                sessionDAO.save(session);
                itemDAO.update(item);
                log.info("Đã tạo phiên đấu giá ID:" + session.getID().toString() + "|SUCCESS");
                return session;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return  null;
    }

    public AuctionSession getSession(UUID ID) throws SQLException {
        AuctionSession re = sessionDAO.get(ID);

        if (re == null) {
            throw new RuntimeException("Không tìm thấy phiên đấu giá");
        } else {
            return re;
        }
    }


    public void placeBid(AuctionSession auctionSession, Bidder bidder, long amount) throws IllegalArgumentException,NullPointerException, SQLException {
        if(auctionSession == null || bidder == null){
            throw new NullPointerException("Null tham số");
        }

        SystemLogger.getInstance().warning("PlaceBid in here 1");

        //Logic kiểm tra phiên có trong db chưa?
        //Thêm observer
//        ObserverDAO observerDAO = new ObserverDAO();
//        observerDAO.addObserverToSession(bidder.getID(),auctionSession.getID());

        SystemLogger.getInstance().warning("PlaceBid in here 2");

        //Cơ chế extend thời gian
        synchronized (AuctionManager.class) {
            if(auctionSession.getTopBid() == null) {
                log.warning("==Null first");

                auctionSession.placeBid(bidder, amount);

                log.warning("inPlaceBid1 1");
                sessionDAO.update(auctionSession);
                log.warning("inPlaceBid1 2");

                System.out.println(bidder.getID());
                System.out.println(bidder.getWalletID());
                walletManager.lockMoney(bidder.getWalletID(), bidder.getID(), amount);

                SystemLogger.getInstance().warning("PlaceBid in here 3");
            }else {
                auctionSession.placeBid(bidder,amount);
                sessionDAO.update(auctionSession);

                BidTicket second = BidHistory.getSecondBySessionID(auctionSession.getID());
                UUID secondBidderID = second.getBidder();
                UUID secondWallet = walletManager.getWalletbyOwner(secondBidderID).getID();
                long money = second.getAmount();

                walletManager.unlockMoney(secondWallet,secondBidderID,money);
                walletManager.lockMoney(bidder.getWalletID(), bidder.getID(), amount);

                SystemLogger.getInstance().warning("PlaceBid in here 4");

            }
            log.info("Đặt giá thầu thành công: " + amount);
        }
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
        itemDAO.update(session.getItem());
        log.info("Phiên đấu giá ID:" + session.getID().toString() + " đã kết thúc");

        //Tiến hành xóa observers
        ObserverDAO observerDAO = new ObserverDAO();
        observerDAO.clearObserversForSession(session.getID());
    }

    public void runSession(AuctionSession session) throws SQLException {
        session.runSession();
        sessionDAO.update(session);
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

        ObserverDAO observerDAO = new ObserverDAO();
        observerDAO.clearObserversForSession(session.getID());
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

    public List<AuctionSession> getSessionUpcoming() throws SQLException{
        List<AuctionSession> list = sessionDAO.getStartingSession();
        if(list == null){
            throw new NullPointerException("Không còn phiên sắp mở");
        }
        return list;
    }

    public List<AuctionSession> getSessionsAll() throws SQLException{
        List<AuctionSession> list = sessionDAO.getAll();
        if(list.isEmpty()){
            throw new NullPointerException("Không tồn tại phiên đấu giá");
        }
        return list;
    }

}
