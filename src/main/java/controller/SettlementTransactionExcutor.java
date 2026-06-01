package controller;

import function.TransactionExcutor;
import function.TransactionStatus;
import models.SettlementTransaction;
import models.Transaction;

import java.sql.SQLException;
import java.util.UUID;

public class SettlementTransactionExcutor implements TransactionExcutor {
    @Override
    public void excute(Transaction transaction, WalletManager walletManager) throws IllegalArgumentException {
        if (transaction instanceof SettlementTransaction) {
            SettlementTransaction settlementTransaction = ((SettlementTransaction) transaction);
            UUID walletID1 = settlementTransaction.getSenderWalletID();
            UUID walletID2 = settlementTransaction.getReceiverWalletID();
            UUID userID1 = settlementTransaction.getSenderID();
            UUID userID2 = settlementTransaction.getReceiverID();
            long money = settlementTransaction.getAmount();

            try {
                walletManager.unlockMoney(walletID1,userID1,money);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                walletManager.transferMoney(walletID1, walletID2, userID1, userID2, money);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        } else {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            throw new IllegalArgumentException("Transaction is not type available");
        }
    }
}