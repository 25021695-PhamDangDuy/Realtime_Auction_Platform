package controller;

import controller.brain.WalletManager;
import database.TransactionDAO;
import database.WithdrawTransactionDAO;
import function.TransactionExcutor;
import function.TransactionStatus;
import models.Transaction;
import models.WithdrawTransaction;

import java.sql.SQLException;
import java.util.UUID;

public class WithdrawTransactionExcutor implements TransactionExcutor {
    private TransactionDAO transactionDAO = new WithdrawTransactionDAO();
    @Override
    public void excute(Transaction transaction, WalletManager walletManager) throws SQLException,IllegalArgumentException,NullPointerException {
        if(transaction instanceof WithdrawTransaction){
            UUID walletID = transaction.getSenderWalletID();
            UUID senderID = transaction.getSenderID();
            long amount = transaction.getAmount();

            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
            walletManager.withdrawWallet(walletID,senderID,amount);

            transactionDAO.update(transaction);
        }else{
            throw new IllegalArgumentException("Loại giao dịch không phù hợp");        }
    }
}
