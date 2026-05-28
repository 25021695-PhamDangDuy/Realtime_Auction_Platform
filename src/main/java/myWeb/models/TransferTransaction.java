package myWeb.models;

import myWeb.function.TransactionStatus;
import myWeb.function.TransactionType;

import java.time.LocalDateTime;

public class TransferTransaction extends Transaction{
    private String receiverWalletID;
    private String receiverID;

    public String getReceiverWalletID(){return receiverWalletID;}
    public String getReceiverID() {
        return receiverID;
    }

    public TransferTransaction(String ID, String senderID, String receiverID, String senderWalletID, String receiverWalletID, double amount){
        super(ID,amount,senderWalletID,senderID,LocalDateTime.now());
        this.receiverWalletID = receiverWalletID;
        this.transactionType = TransactionType.TRANSFER_MONEY;
        this.transactionStatus = TransactionStatus.PENDING;
        this.receiverID = receiverID;
    }
}
