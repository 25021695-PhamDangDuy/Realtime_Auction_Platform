package models;

import function.BidStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/*
Class này sẽ hoạt động như một tấm vé xác định giao dịch trong một phiên của Bidder
 */
public class BidTicket {
    private final UUID ID;
    private final UUID bidderID; //Nguoi dat Bid
    private final UUID sessionID; //Phien Bidder dat
    private final LocalDateTime timeBid; //Thoi gian dat Bid
    private final long amount; //So tien dat Bid
    private BidStatus status;

    //Sau khi set final thi chi duy nhat constructor la dat data
    public BidTicket(UUID bidder, UUID as, LocalDateTime t, long a, BidStatus bs){
        this.ID = UUID.randomUUID();
        this.bidderID = bidder;
        sessionID = as;
        timeBid = t;
        amount = a;
        status = bs;
    }
    public BidTicket(UUID id, UUID bidder, UUID as, LocalDateTime t, long a, BidStatus bs){
        this.ID = id;
        this.bidderID = bidder;
        sessionID = as;
        timeBid = t;
        amount = a;
        status = bs;
    }

    public UUID getID(){return  ID;}
    public UUID getBidder() {
        return bidderID;
    }
    public UUID getSession() {
        return sessionID;
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
