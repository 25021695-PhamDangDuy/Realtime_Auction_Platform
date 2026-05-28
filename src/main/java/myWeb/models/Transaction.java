package myWeb.models;

import myWeb.function.TransactionStatus;
import myWeb.function.TransactionType;

import java.time.LocalDateTime;

/*
Interface này làm một nhiệm vụ, giống BidTicket: lưu các thao tác giao dịch liên quan đến tài chính, ví, tiền thành bằng chứng
Sau này sẽ tiện cho việc truy xuất
Vì vậy để bảo đảm an toàn dữ liệu Thiết yếu, ta sẽ cần settup sao cho nó không được phép sinh sai sót dữ liệu khi đã khởi tạo thành công
 */
public abstract class Transaction {
    private String transactionID;       //Mã giao dịch
    private double amount;              //Số tiền giao dịch
    private String senderWalletID;      //mã ID ví người gửi
    private String senderID;            //mã ID người gửi
    private LocalDateTime timestamp;       //thời gian thực hiện
    protected TransactionType transactionType;      //Loại giao dịch
    protected TransactionStatus transactionStatus;          //Trạng thái giao dịch

    public String getTransactionID(){return transactionID;}
    public double getAmount(){return amount;}
    public TransactionStatus getTransactionStatus(){return transactionStatus;}
    public LocalDateTime getTimestamp(){return timestamp;}
    public String getSenderWalletID(){return senderWalletID;}
    public String getSenderID(){return  senderID;}

    public Transaction(String ID, double amount, String walletID,String senderID, LocalDateTime time){
        this.transactionID = ID;
        this.amount = amount;
        this.senderWalletID = walletID;
        this.timestamp = time;
        this.transactionStatus = TransactionStatus.PENDING;
        this.senderID = senderID;
    }
    public Transaction(){}  // Mở rộng khả năng đa hình hàm constructor cho các class con

    /*
    Một giao dịch bất kì, thì đều phải có sự chuyển dịch dòng tiền
     */
    public void setTransactionStatus(TransactionStatus transactionStatus){
        this.transactionStatus = transactionStatus;
    }
}
