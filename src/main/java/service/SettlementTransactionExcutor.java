package service;

import service.brain.WalletManager;
import database.SettlementTransactionDAO;
import function.TransactionExcutor;
import function.TransactionStatus;
import models.SettlementTransaction;
import models.Transaction;

import java.sql.SQLException;
import java.util.UUID;

public class SettlementTransactionExcutor implements TransactionExcutor {
    private SettlementTransactionDAO settlementTransactionDAO = new SettlementTransactionDAO();
    @Override
    public void excute(Transaction transaction, WalletManager walletManager) throws IllegalArgumentException, SQLException {
        if (transaction instanceof SettlementTransaction settlementTransaction) {
            UUID walletID1 = settlementTransaction.getSenderWalletID();
            UUID walletID2 = settlementTransaction.getReceiverWalletID();
            UUID userID1 = settlementTransaction.getSenderID();
            UUID userID2 = settlementTransaction.getReceiverID();
            long money = settlementTransaction.getAmount();

            walletManager.unlockMoney(walletID1,userID1,money);
            walletManager.transferMoney(walletID1, walletID2, userID1, userID2, money);
            settlementTransaction.setTransactionStatus(TransactionStatus.SUCCESS);

            settlementTransactionDAO.update(settlementTransaction);

        } else {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            throw new IllegalArgumentException("Transaction is not type available");
        }
    }
}