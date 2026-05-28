package myWeb.controller;

import myWeb.function.TransactionExcutor;
import myWeb.models.DepositTransaction;
import myWeb.models.Transaction;
import myWeb.models.WithdrawTransaction;

public class DepositTransactionExcutor implements TransactionExcutor {
    @Override
    public void excute(Transaction transaction, WalletManager walletManager) {
        if(transaction instanceof DepositTransaction){
            String walletID = transaction.getSenderWalletID();
            String senderID = transaction.getSenderID();
            double amount = transaction.getAmount();
            walletManager.depositWallet(walletID,senderID,amount);
        }else{
            throw new IllegalArgumentException("Transaction is not avaiable");
        }
    }
}
