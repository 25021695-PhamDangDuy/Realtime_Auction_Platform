package myWeb.controller;

import myWeb.function.TransactionExcutor;
import myWeb.models.Transaction;
import myWeb.models.WithdrawTransaction;

public class WithdrawTransactionExcutor implements TransactionExcutor {

    @Override
    public void excute(Transaction transaction, WalletManager walletManager) {
        if(transaction instanceof WithdrawTransaction){
            String walletID = transaction.getSenderWalletID();
            String senderID = transaction.getSenderID();
            double amount = transaction.getAmount();
            walletManager.withdrawWallet(walletID,senderID,amount);
        }else{
            throw new IllegalArgumentException("Transaction is not avaiable");
        }
    }
}
