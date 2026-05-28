package myWeb.models;

import myWeb.function.TransactionStatus;
import myWeb.function.TransactionType;

import java.time.LocalDateTime;

public class WithdrawTransaction extends Transaction{
    public WithdrawTransaction(String ID,double amount, String senderWalletID, String senderID){
        super(ID, amount,senderWalletID,senderID, LocalDateTime.now());
        this.transactionType = TransactionType.WITHDRAW_WALLET;
        this.transactionStatus = TransactionStatus.PENDING;
    }
}
