package myWeb.controller;

import myWeb.models.Bidder;
import myWeb.models.Item;
import myWeb.models.Seller;

import java.time.LocalDateTime;

public class AuctionSession {
    private String produceId;
    private Item item;
    private Seller seller;

    private double currentPrice;//giá hiện tại
    private double minIncreament; // bước giá tối thiểu
    private Bidder topBidder; //người trả giá cao nhất hiện tại

    private LocalDateTime endTime;// thời gian kết thúc
    private boolean isActive=true;// trạng thái phiên

    public AuctionSession(String produceId,Item item,Seller seller,double startPrice,double minIncreament,LocalDateTime endtime) {
        this.produceId = produceId;
        this.item=item;
        this.seller=seller;
        this.currentPrice=startPrice;
        this.minIncreament=minIncreament;
        this.endTime=endtime;

    }

    public String getProduceId() {
        return produceId;
    }

    public synchronized void placeBid(Bidder bidder, double bidAmount) throws IllegalArgumentException{
        // 1. Kiểm tra thời gian & trạng thái
        if (!isActive || LocalDateTime.now().isAfter(endTime)) {
            this.isActive = false;
            throw new IllegalArgumentException("Phiên đấu giá đã kết thúc!");
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
        double requiredMinBid = (topBidder == null) ? currentPrice : currentPrice + minIncreament;
        if (bidAmount < requiredMinBid) {
            throw new IllegalArgumentException("Giá thầu phải từ " + requiredMinBid + " trở lên!");
        }
        // 5. Nếu vượt qua mọi bài kiểm tra -> Cập nhật thành công!
        this.currentPrice = bidAmount;
        this.topBidder = bidder;
    }

}

