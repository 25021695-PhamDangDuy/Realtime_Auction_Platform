package models;

import function.*;
import service.AuctionObserver;
import database.BidTicketDAO;
import database.ObserverDAO;
import service.brain.AccountController;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AuctionSession {
    private UUID ID;
    private Item item;
    private Seller seller;

    private long currentPrice;//giá hiện tại
    private BidTicket topBid;
    private long minIncrement; // bước giá tối thiểu


    private LocalDateTime startTime;// Thời gian bắt đầu
    private LocalDateTime endTime;// thời gian kết thúc
    private SessionStatus status;// trạng thái phiên

    private transient SessionChecker sessionChecker = new SessionChecker();
    private transient BidTicketDAO bidTicketDAO = new BidTicketDAO();
    private transient AccountController accountController = AccountController.getInstance();
    //Tạo
    public AuctionSession(Item item,Seller seller,long startPrice,long minIncrement,LocalDateTime endTime,LocalDateTime startTime, SessionStatus status) {
        this.ID = UUID.randomUUID();
        this.item=item;
        this.seller=seller;
        this.currentPrice=startPrice;
        this.minIncrement=minIncrement;
        this.endTime=endTime;
        this.startTime = startTime;
        this.status = status;

    }
    public AuctionSession(UUID id,Item item,Seller seller,long startPrice,long minIncrement,LocalDateTime endTime,LocalDateTime startTime, SessionStatus status, BidTicket bidTicket) {
        this.ID = id;
        this.item=item;
        this.seller=seller;
        this.currentPrice=startPrice;
        this.minIncrement=minIncrement;
        this.endTime=endTime;
        this.startTime = startTime;
        this.status = status;
        this.topBid = bidTicket;
    }
    //hàm đăng ký theo dõi/ hủy theo dõi phiên đấu giá
    public void attach(AuctionObserver observer) throws SQLException {
        ObserverDAO observerDAO = new ObserverDAO();
        observerDAO.addObserverToSession((observer.getID()),ID);
    }
    public void detach(AuctionObserver observer) throws SQLException {
        ObserverDAO observerDAO = new ObserverDAO();
        observerDAO.removeObserverFromSession(observer.getID(),ID);
    }

    public List<AuctionObserver> getObserver() throws SQLException {
        ObserverDAO observerDAO = new ObserverDAO();
        return observerDAO.getObserversBySessionID(ID);
    }

    public int getNumberObserver() throws SQLException {
        ObserverDAO observerDAO = new ObserverDAO();
        return observerDAO.getObserverCountBySessionID(ID);
    }

    //gửi thông báo
//    public void notifyObservers(String message){
//        for (AuctionObserver observer : observers){
//            observer.update(message);
//        }
//    }
    public void broadcastToAll(String message){
        System.out.println("Broadcast to all server:" + message);
    }

    //Getter
    public UUID getID() {
        return ID;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public SessionStatus getStatus() {
        return status;
    }
    public long getMinIncrement(){return minIncrement;}
    public User getTopBidder() throws SQLException {
        if (topBid != null) {
            User top = accountController.getInfor(topBid.getBidder());
            return top;
        }else{
            return null;
        }
    }
    public Item getItem() {
        return item;
    }
    public Seller getSeller() {
        return seller;
    }
    public long getCurrentPrice(){return currentPrice;}
    public BidTicket getTopBid(){return topBid;}

    public void setStatus(SessionStatus status) throws NullPointerException {
        if(status == null){
            throw new NullPointerException("status parameter is null");
        }
        this.status = status;
    }

    public synchronized void placeBid(Bidder bidder, long bidAmount) throws IllegalArgumentException, SQLException {

        SystemLogger.getInstance().warning("AS method placebid in 1");
        // 1. Kiểm tra thời gian & trạng thái
        if (!sessionChecker.isAuctioning(this)) {
            throw new IllegalArgumentException(status.getDescription());
        }

        SystemLogger.getInstance().warning("AS method placebid in 2");
        // 2. Chống gian lận: Người bán tự đẩy giá (Shill Bidding)
        if (bidder.getID().equals(seller.getID())) {
            throw new IllegalArgumentException("Người bán không thể tự đấu giá món đồ của mình!");
        }
        SystemLogger.getInstance().warning("AS method placebid in 3");
        // 3. Tránh tự "đấu" chính mình
        if (this.getTopBidder() != null && bidder.getID().equals(this.getTopBidder().getID())) {
            throw new IllegalArgumentException("Bạn đang là người giữ giá cao nhất rồi!");
        }
        SystemLogger.getInstance().warning("AS method placebid in 4");
        // 4. Kiểm tra số tiền: Phải lớn hơn hoặc bằng (Giá hiện tại + Bước giá)
        double requiredMinBid = (this.getTopBidder() == null) ? currentPrice : currentPrice + minIncrement;
        if (bidAmount < requiredMinBid) {
            throw new IllegalArgumentException("Giá thầu phải từ " + requiredMinBid + " trở lên!");
        }


        SystemLogger.getInstance().warning("AS method placebid in 5");
        // 5. Nếu vượt qua mọi bài kiểm tra -> Cập nhật thành công!
        LocalDateTime time = LocalDateTime.now();
        this.currentPrice = bidAmount;

        SystemLogger.getInstance().warning("AS method placebid in 6");
        BidTicket newTicket = new BidTicket(bidder.getID(),this.ID,time,bidAmount,BidStatus.VALID);
        bidTicketDAO.save(newTicket);
        topBid = newTicket;

        SystemLogger.getInstance().warning("AS method placebid in 7");
        if(sessionChecker.isExtendTime(this,time)){
            extendEndTime(endTime.plusMinutes(10));
        }
    }

    public void extendEndTime(LocalDateTime newEndTime) throws NullPointerException{
        SessionChecker sessionChecker = new SessionChecker();
        if(newEndTime == null){
            throw new NullPointerException("Null tham số");
        }
        /*
        Điều kiện để mở giới hạn thời gian:
        1. endTime cũ phải trước endTime mới
        2. khoảng cách tối thiểu phải lớn hơn hoặc 1 phút và tối đa 10 ngày
         */
        if (sessionChecker.durationTime(endTime,newEndTime,1,14400)){
            endTime = newEndTime;
        }
    }
    public void finishSession() throws IllegalArgumentException, SQLException {
        if(sessionChecker.isSessionTimeUp(this)){
            status = SessionStatus.FINISHED;
            item.setItemStatus(ItemStatus.SOLD);
            User winner = this.getTopBidder();
            Item reward = item;

            reward.setItemStatus(ItemStatus.SOLD);
        }else{
            throw new IllegalArgumentException("Session is not timeup!");
        }
    }

    public void cancelSession() throws IllegalArgumentException {
        if(status != SessionStatus.CANCELED){
            status = SessionStatus.CANCELED;
            item.setItemStatus(ItemStatus.AVAILABLE);
        }else {
            throw new IllegalArgumentException("This session have canceled");
        }
    }

    public void pendSession() throws IllegalArgumentException{
        /*
        Điều kiện dùng phiên là: phiên đang chạy
         */
        if(sessionChecker.isAuctioning(this) && !sessionChecker.isSessionTimeUp(this)){
            status = SessionStatus.PENDING;
        }else {
            throw new IllegalArgumentException("Session is not Auctioning");
        }
    }

    public void unPendSession() throws IllegalArgumentException {
        if(status == SessionStatus.PENDING){
            status = SessionStatus.RUNNING;
        }else {
            throw new IllegalArgumentException("Session is not Pending");
        }
    }

    public void runSession() throws IllegalArgumentException {
        if(sessionChecker.isUpComing(this) && sessionChecker.isRunSession(this,LocalDateTime.now())){
            status = SessionStatus.RUNNING;
        } else {
            throw new IllegalArgumentException("Session is not upcoming");

    }

    }
    // Gửi thông báo cho toàn bộ khán giả trong phòng
    public void notifyBidObservers(String message) throws SQLException {
        // Lặp qua danh sách (List) những người đang theo dõi
        for (AuctionObserver observer : this.getObserver()) {
            // Gọi cái "tai" của từng người để nhét tin nhắn vào
            observer.update(message);
        }
    }
}

