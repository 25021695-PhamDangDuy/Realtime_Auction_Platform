package myWeb.controller;

import myWeb.function.SessionStatus;
import myWeb.models.Bidder;
import myWeb.models.Item;
import myWeb.models.Seller;

import java.time.LocalDateTime;

public class AuctionSession {
    private String productId;
    private Item item;
    private Seller seller;

    private double currentPrice;//giá hiện tại
    private double minIncrement; // bước giá tối thiểu
    private Bidder topBidder; //người trả giá cao nhất hiện tại

    private LocalDateTime startTime;// Thời gian bắt đầu
    private LocalDateTime endTime;// thời gian kết thúc
    private SessionStatus status;// trạng thái phiên

    //Tạo
    public AuctionSession(String productId,Item item,Seller seller,double startPrice,double minIncrement,LocalDateTime endTime,LocalDateTime startTime, SessionStatus status) {
        this.productId = productId;
        this.item=item;
        this.seller=seller;
        this.currentPrice=startPrice;
        this.minIncrement=minIncrement;
        this.endTime=endTime;
        this.startTime = startTime;
        this.status = status;
    }

    public String getProduceId() {
        return productId;
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
    public Bidder getTopBidder() {
        return topBidder;
    }
    public Item getItem() {
        return item;
    }
    public Seller getSeller() {
        return seller;
    }

    public void setStatus(SessionStatus status) throws NullPointerException {
        if(status == null){
            throw new NullPointerException("status parameter is null");
        }
        this.status = status;
    }

    public synchronized void placeBid(Bidder bidder, double bidAmount) throws IllegalArgumentException{
        // 1. Kiểm tra thời gian & trạng thái
        if (!status.equals(SessionStatus.RUNNING)) {
            throw new IllegalArgumentException(status.getDescription());
        }
        // 2. Chống gian lận: Người bán tự đẩy giá (Shill Bidding)
        if (bidder.getID().equals(seller.getID())) {
            throw new IllegalArgumentException("Người bán không thể tự đấu giá món đồ của mình!");
        }

        // 3. Tránh tự "đấu" chính mình
        if (topBidder != null && bidder.getID().equals(topBidder.getID())) {
            throw new IllegalArgumentException("Bạn đang là người giữ giá cao nhất rồi!");
        }
        // 4. Kiểm tra số tiền: Phải lớn hơn hoặc bằng (Giá hiện tại + Bước giá)
        double requiredMinBid = (topBidder == null) ? currentPrice : currentPrice + minIncrement;
        if (bidAmount < requiredMinBid) {
            throw new IllegalArgumentException("Giá thầu phải từ " + requiredMinBid + " trở lên!");
        }
        // 5. Nếu vượt qua mọi bài kiểm tra -> Cập nhật thành công!
        this.currentPrice = bidAmount;
        this.topBidder = bidder;
    }

}

