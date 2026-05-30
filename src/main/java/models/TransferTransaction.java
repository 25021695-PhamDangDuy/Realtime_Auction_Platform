package models;

import function.TransactionStatus;
import function.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransferTransaction extends Transaction{
    private UUID receiverWalletID;
    private UUID receiverID;

    public UUID getReceiverWalletID(){return receiverWalletID;}
    public UUID getReceiverID() {
        return receiverID;
    }

    public TransferTransaction(UUID senderID, UUID receiverID, UUID senderWalletID, UUID receiverWalletID, long amount){
        super(amount,senderWalletID,senderID,LocalDateTime.now());
        this.receiverWalletID = receiverWalletID;
        this.transactionType = TransactionType.TRANSFER_MONEY;
        this.transactionStatus = TransactionStatus.PENDING;
        this.receiverID = receiverID;
    }
}
