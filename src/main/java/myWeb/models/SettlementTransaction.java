package myWeb.models;

import myWeb.function.TransactionStatus;
import myWeb.function.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

/*
Hàm này sẽ khởi tạo loại giao dịch dạng quyết toán hợp đồng với phiên đấu giá và Seller từ Bidder winner;
 */
public class SettlementTransaction extends Transaction{
    private AuctionSession session;
    private UUID receiverWalletID;
    private UUID receiverID;

    public AuctionSession getSession(){return session;}
    public UUID getReceiverWalletID(){return receiverWalletID;}
    public UUID getReceiverID() {
        return receiverID;
    }

    public SettlementTransaction(AuctionSession session){
        super(session.getCurrentPrice(),session.getTopBidder().getWalletID(),session.getTopBidder().getID(),LocalDateTime.now());
        this.session = session;
        this.receiverWalletID = session.getSeller().getWalletID();
        this.transactionType = TransactionType.AUCTION_SETTLEMENT;
        this.transactionStatus = TransactionStatus.PENDING;
        this.receiverID = session.getSeller().getID();
    }

}
