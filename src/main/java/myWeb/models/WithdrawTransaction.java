package myWeb.models;

import myWeb.function.TransactionStatus;
import myWeb.function.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public class WithdrawTransaction extends Transaction{
    public WithdrawTransaction(long amount, UUID senderWalletID, UUID senderID){
        super(amount,senderWalletID,senderID, LocalDateTime.now());
        this.transactionType = TransactionType.WITHDRAW_WALLET;
        this.transactionStatus = TransactionStatus.PENDING;
    }
}
