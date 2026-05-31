package myWeb.models;

import myWeb.function.TransactionStatus;
import myWeb.function.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public class DepositTransaction extends Transaction{
    public DepositTransaction (long amount, UUID senderWalletID, UUID senderID){
        super(amount,senderWalletID,senderID, LocalDateTime.now());
        this.transactionType = TransactionType.DEPOSIT_WALLET;
        this.transactionStatus = TransactionStatus.PENDING;
    }
}
