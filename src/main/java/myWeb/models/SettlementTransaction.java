package myWeb.models;

import myWeb.function.TransactionStatus;
import myWeb.function.TransactionType;

import java.time.LocalDateTime;

/*
Hàm này sẽ khởi tạo loại giao dịch dạng quyết toán hợp đồng với phiên đấu giá và Seller từ Bidder winner;
 */
public class SettlementTransaction extends Transaction{
    private AuctionSession session;
    private String receiverWalletID;
    private String receiverID;

    public AuctionSession getSession(){return session;}
    public String getReceiverWalletID(){return receiverWalletID;}
    public String getReceiverID() {
        return receiverID;
    }

    public SettlementTransaction(String ID, AuctionSession session){
        super(ID,session.getCurrentPrice(),session.getTopBidder().getWalletID(),session.getTopBidder().getID(),LocalDateTime.now());
        this.session = session;
        this.receiverWalletID = session.getSeller().getWalletID();
        this.transactionType = TransactionType.AUCTION_SETTLEMENT;
        this.transactionStatus = TransactionStatus.PENDING;
        this.receiverID = session.getSeller().getID();
    }

}
