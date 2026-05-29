package myWeb.models;

import myWeb.function.BidStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/*
Class này sẽ hoạt động như một tấm vé xác định giao dịch trong một phiên của Bidder
 */
public class BidTicket {
    private final UUID ID;
    private final Bidder bidder; //Nguoi dat Bid
    private final AuctionSession session; //Phien Bidder dat
    private final LocalDateTime timeBid; //Thoi gian dat Bid
    private final long amount; //So tien dat Bid
    private BidStatus status;

    //Sau khi set final thi chi duy nhat constructor la dat data
    public BidTicket(Bidder bidder, AuctionSession as, LocalDateTime t, long a, BidStatus bs){
        this.ID = UUID.randomUUID();
        this.bidder = bidder;
        session = as;
        timeBid = t;
        amount = a;
        status = bs;
    }
    public BidTicket(UUID id, Bidder bidder, AuctionSession as, LocalDateTime t, long a, BidStatus bs){
        this.ID = id;
        this.bidder = bidder;
        session = as;
        timeBid = t;
        amount = a;
        status = bs;
    }

    public UUID getID(){return  ID;}
    public Bidder getBidder() {
        return bidder;
    }
    public AuctionSession getSession() {
        return session;
    }
    public long getAmount() {
        return amount;
    }
    public LocalDateTime getTimeBid() {
        return timeBid;
    }
    public BidStatus getStatus() {
        return status;
    }
}
