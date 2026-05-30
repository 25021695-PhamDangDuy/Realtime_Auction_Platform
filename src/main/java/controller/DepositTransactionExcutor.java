package controller;

import function.TransactionExcutor;
import models.DepositTransaction;
import models.Transaction;
import java.util.UUID;

public class DepositTransactionExcutor implements TransactionExcutor {
    @Override
    public void excute(Transaction transaction, WalletManager walletManager) {
        if(transaction instanceof DepositTransaction){
            UUID walletID = transaction.getSenderWalletID();
            UUID senderID = transaction.getSenderID();
            long amount = transaction.getAmount();
            walletManager.depositWallet(walletID,senderID,amount);
        }else{
            throw new IllegalArgumentException("Transaction is not avaiable");
        }
    }
}
