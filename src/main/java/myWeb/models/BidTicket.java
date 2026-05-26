package myWeb.models;

import myWeb.function.BidStatus;

import java.time.LocalDateTime;

/*
Class này sẽ hoạt động như một tấm vé xác định giao dịch trong một phiên của Bidder
 */
public class BidTicket {
    private final Bidder bidder; //Nguoi dat Bid
    private final AuctionSession session; //Phien Bidder dat
    private final LocalDateTime timeBid; //Thoi gian dat Bid
    private final Double amount; //So tien dat Bid
    private BidStatus status;

    //Sau khi set final thi chi duy nhat constructor la dat data
    public BidTicket(Bidder bidder, AuctionSession as, LocalDateTime t, Double a, BidStatus bs){
        this.bidder = bidder;
        session = as;
        timeBid = t;
        amount = a;
        status = bs;
    }

    public Bidder getBidder() {
        return bidder;
    }
    public AuctionSession getSession() {
        return session;
    }
    public Double getAmount() {
        return amount;
    }
    public LocalDateTime getTimeBid() {
        return timeBid;
    }
    public BidStatus getStatus() {
        return status;
    }
}
