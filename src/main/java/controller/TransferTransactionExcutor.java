package controller;

import function.TransactionExcutor;
import function.TransactionStatus;
import models.Transaction;
import models.TransferTransaction;

import java.sql.SQLException;
import java.util.UUID;

public class TransferTransactionExcutor implements TransactionExcutor {
    @Override
    public void excute(Transaction transaction, WalletManager walletManager) throws IllegalArgumentException, SQLException, NullPointerException {
        if (transaction instanceof TransferTransaction) {
            TransferTransaction transferTransaction = ((TransferTransaction) transaction);

            UUID walletID1 = transferTransaction.getSenderWalletID();
            UUID walletID2 = transferTransaction.getReceiverWalletID();
            UUID userID1 = transferTransaction.getSenderID();
            UUID userID2 = transferTransaction.getReceiverID();
            long money = transferTransaction.getAmount();

            walletManager.transferMoney(walletID1, walletID2, userID1, userID2, money);
            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        } else {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            throw new IllegalArgumentException("Loại giao dịch không phù hợp");
        }
    }
}
