package models;

import function.TransactionStatus;
import function.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public class DepositTransaction extends Transaction{
    public DepositTransaction (long amount, UUID senderWalletID, UUID senderID){
        super(amount,senderWalletID,senderID, LocalDateTime.now());
        this.transactionType = TransactionType.DEPOSIT_WALLET;
        this.transactionStatus = TransactionStatus.PENDING;
    }

    public DepositTransaction(UUID transactionID, long amount, UUID senderWalletID, UUID senderID,
                              LocalDateTime timestamp, TransactionStatus status) {
        super(transactionID, amount, senderWalletID, senderID, timestamp, status);
        this.transactionType = TransactionType.DEPOSIT_WALLET;
    }
}
