package models;


import function.TransactionStatus;
import function.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

/*
Class chứa dữ liệu thay thế cho hàm khởi tạo của Transaction cho gọn hơn, tránh vi phạm qui tắc long parameters
 */
public class TransactionDetails {
    private UUID senderID;
    private UUID receiverID = null;
    private UUID senderWalletID;
    private UUID receiverWalletID = null;
    private long amount;
    private LocalDateTime timestamp;       //thời gian thực hiện
    protected TransactionType transactionType;      //Loại giao dịch
    protected TransactionStatus transactionStatus;          //Trạng thái giao dịch

    //Getter

    public UUID getReceiverID() {
        if(receiverID == null){
            throw new NullPointerException("Giao dịch không có người nhận");
        }
        return receiverID;
    }
    public UUID getSenderID() {
        return senderID;
    }
    public UUID getReceiverWalletID() {
        if(receiverWalletID == null){
            throw new NullPointerException("Giao dịch không có người nhận");
        }
        return receiverWalletID;
    }
    public UUID getSenderWalletID() {
        return senderWalletID;
    }
    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }
    public TransactionType getTransactionType() {
        return transactionType;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public long getAmount() {
        return amount;
    }

    //Putter: cơ chế đẩy thuộc tính cần thiết vào class này để làm kho dữ liệu khởi tạo các transaction
    public void putSenderID(UUID ID){this.senderID = ID;}
    public void putSenderWalletID(UUID ID){this.senderWalletID = ID;}
    public void putReceiverID(UUID ID){this.receiverID = ID;}
    public void putReceiverWalletID(UUID ID){this.receiverWalletID = ID;}
    public void putTime(LocalDateTime time){this.timestamp = time;}
    public void putStatus(TransactionStatus status){this.transactionStatus = status;}
    public void putType(TransactionType type){this.transactionType = type;}
}
