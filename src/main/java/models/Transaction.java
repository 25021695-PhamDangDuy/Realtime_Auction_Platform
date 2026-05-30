package models;

import function.TransactionStatus;
import function.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

/*
Interface này làm một nhiệm vụ, giống BidTicket: lưu các thao tác giao dịch liên quan đến tài chính, ví, tiền thành bằng chứng
Sau này sẽ tiện cho việc truy xuất
Vì vậy để bảo đảm an toàn dữ liệu Thiết yếu, ta sẽ cần settup sao cho nó không được phép sinh sai sót dữ liệu khi đã khởi tạo thành công
 */
public abstract class Transaction {
    private UUID transactionID;       //Mã giao dịch
    private long amount;              //Số tiền giao dịch
    private UUID senderWalletID;      //mã ID ví người gửi
    private UUID senderID;            //mã ID người gửi
    private LocalDateTime timestamp;       //thời gian thực hiện
    protected TransactionType transactionType;      //Loại giao dịch
    protected TransactionStatus transactionStatus;          //Trạng thái giao dịch

    public UUID getID(){return transactionID;}
    public long getAmount(){return amount;}
    public TransactionStatus getTransactionStatus(){return transactionStatus;}
    public LocalDateTime getTimestamp(){return timestamp;}
    public UUID getSenderWalletID(){return senderWalletID;}
    public UUID getSenderID(){return  senderID;}

    public Transaction(long amount, UUID walletID,UUID senderID, LocalDateTime time){
        this.transactionID = UUID.randomUUID();
        this.amount = amount;
        this.senderWalletID = walletID;
        this.timestamp = time;
        this.transactionStatus = TransactionStatus.PENDING;
        this.senderID = senderID;
    }

    protected Transaction(UUID transactionID, long amount, UUID walletID, UUID senderID, LocalDateTime time, TransactionStatus status) {
        this.transactionID = transactionID;
        this.amount = amount;
        this.senderWalletID = walletID;
        this.senderID = senderID;
        this.timestamp = time;
        this.transactionStatus = status;
    }

    public Transaction(){}  // Mở rộng khả năng đa hình hàm constructor cho các class con

    /*
    Một giao dịch bất kì, thì đều phải có sự chuyển dịch dòng tiền
     */
    public void setTransactionStatus(TransactionStatus transactionStatus){
        this.transactionStatus = transactionStatus;
    }
}
