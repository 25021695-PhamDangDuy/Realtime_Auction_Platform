package myWeb.controller;

import myWeb.function.TransactionExcutor;
import myWeb.function.TransactionStatus;
import myWeb.function.TransactionType;
import myWeb.models.*;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/*
Đây sẽ là lớp quản lí và gọi các hàm liên quan tài chính như Wallet, Transaction,v,v
 */
public class PaymentManager {
    private WalletManager walletManager;
    private static PaymentManager instance;
    private TransactionExcutor excutor;
    private ConcurrentHashMap<String, Transaction> transactionHistory; // Lưu lịch sử
    private ConcurrentHashMap<Class<? extends Transaction>, TransactionExcutor> strategies;

    private PaymentManager(){
        walletManager = WalletManager.getInstance();
        strategies = new ConcurrentHashMap<>();
        this.addStrategy();
    }
    public  static PaymentManager getInstance() {
        if(instance == null){
            synchronized (PaymentManager.class){
                instance = new PaymentManager();
                return instance;
            }
        }else {
            return instance;
        }
    }

    private void addStrategy(){
        strategies.put(TransferTransaction.class, new TransferTransactionExcutor());
        strategies.put(SettlementTransaction.class, new SettlementTransactionExcutor());
        strategies.put(DepositTransaction.class, new DepositTransactionExcutor());
        strategies.put(WithdrawTransaction.class, new WithdrawTransactionExcutor());
    }
    // ===== TẠO GIAO DỊCH =====
    /*
    Tiến hành tạo giao dịch bất kì, sử dụng nguyên lí đóng gói để tạo tính đa hình cho method. Từ đó tập trung quản lí thông qua
    thông qua class PaymentManager này
     */
    public Transaction createTransaction(Class<? extends Transaction> type , HashMap<String, Object> params) {
        Transaction transaction = null;

        if (type == TransferTransaction.class) {
            transaction = new TransferTransaction(
                    (String) params.get("ID"),
                    (String) params.get("senderID"),
                    (String) params.get("receiverID"),
                    (String) params.get("senderWalletID"),
                    (String) params.get("receiverWalletID"),
                    (Double) params.get("amount")
            );
        }
        if (type == SettlementTransaction.class) {
            transaction = new SettlementTransaction(
                    (String) params.get("ID"),
                    (AuctionSession) params.get("session")
            );
        }
        if (type == WithdrawTransaction.class) {
            transaction = new WithdrawTransaction(
                    (String) params.get("ID"),
                    (Double) params.get("amount"),
                    (String) params.get("senderID"),
                    (String) params.get("senderWalletID")
            );
        }
        if (type == DepositTransaction.class) {
            transaction = new DepositTransaction(
                    (String) params.get("ID"),
                    (Double) params.get("amount"),
                    (String) params.get("senderID"),
                    (String) params.get("senderWalletID")
            );
        }
        // Thêm các loại giao dịch khác

        if (transaction != null) {
            transaction.setTransactionStatus(TransactionStatus.PENDING);
            transactionHistory.put(transaction.getTransactionID(), transaction);
        }

        return transaction;
    }

    // ===== THỰC THI GIAO DỊCH =====
    public void executeTransaction(Transaction transaction)
            throws IllegalArgumentException {

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        try {
            if (excutor == null) {
                throw new IllegalArgumentException(
                        "No executor found for transaction type: " + transaction.getClass().getSimpleName()
                );
            }

            // Thực thi giao dịch
            TransactionExcutor excutor = strategies.get(transaction.getClass());
            excutor.excute(transaction, walletManager);

        } catch (Exception e) {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            throw new IllegalArgumentException("Transaction execution failed: " + e.getMessage());
        }
    }


    // ===== LOCK TIỀN CHO PHIÊN ĐẤU GIÁ =====
    public void lockMoneyForAuction(String walletID, String ownerID,
                                    double amount)
            throws IllegalArgumentException {
        try {
            walletManager.lockMoney(walletID, ownerID, amount);

        } catch (Exception e) {

            throw new IllegalArgumentException("Cannot lock money: " + e.getMessage());
        }
    }

    // ===== UNLOCK TIỀN =====
    public void unlockMoneyFromAuction(String walletID, String ownerID,
                                       double amount)
            throws IllegalArgumentException {
        try {
            walletManager.unlockMoney(walletID, ownerID, amount);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot unlock money: " + e.getMessage());
        }
    }

}


