package myWeb.controller;

import myWeb.function.TransactionExcutor;
import myWeb.function.TransactionStatus;
import myWeb.models.SettlementTransaction;
import myWeb.models.Transaction;
import myWeb.models.TransferTransaction;

public class TransferTransactionExcutor implements TransactionExcutor {

    @Override
    public void excute(Transaction transaction, WalletManager walletManager) {
        if (transaction instanceof TransferTransaction) {
            TransferTransaction transferTransaction = ((TransferTransaction) transaction);

            String walletID1 = transferTransaction.getSenderWalletID();
            String walletID2 = transferTransaction.getReceiverWalletID();
            String userID1 = transferTransaction.getSenderID();
            String userID2 = transferTransaction.getReceiverID();
            double money = transferTransaction.getAmount();

            walletManager.transferMoney(walletID1, walletID2, userID1, userID2, money);
            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        } else {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            throw new IllegalArgumentException("Transaction is not type available");
        }
    }
}
