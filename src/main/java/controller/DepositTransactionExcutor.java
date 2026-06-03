package controller;

import controller.brain.WalletManager;
import database.DepositTransactionDAO;
import database.TransactionDAO;
import function.TransactionExcutor;
import function.TransactionStatus;
import models.DepositTransaction;
import models.Transaction;

import java.sql.SQLException;
import java.util.UUID;

public class DepositTransactionExcutor implements TransactionExcutor {
    private final TransactionDAO<DepositTransaction> transactionDAO = new DepositTransactionDAO();
    @Override
    public void excute(Transaction transaction, WalletManager walletManager) throws IllegalArgumentException, SQLException, NullPointerException {
        if(transaction instanceof DepositTransaction depositTransaction){
            UUID walletID = transaction.getSenderWalletID();
            UUID senderID = transaction.getSenderID();
            long amount = transaction.getAmount();

            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
            transactionDAO.update(depositTransaction);
        }else{
            throw new IllegalArgumentException("Transaction is not avaiable");
        }
    }
}
