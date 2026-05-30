package controller;

import function.TransactionExcutor;
import models.Transaction;
import models.WithdrawTransaction;
import java.util.UUID;

public class WithdrawTransactionExcutor implements TransactionExcutor {

    @Override
    public void excute(Transaction transaction, WalletManager walletManager) {
        if(transaction instanceof WithdrawTransaction){
            UUID walletID = transaction.getSenderWalletID();
            UUID senderID = transaction.getSenderID();
            long amount = transaction.getAmount();
            walletManager.withdrawWallet(walletID,senderID,amount);
        }else{
            throw new IllegalArgumentException("Transaction is not avaiable");
        }
    }
}
