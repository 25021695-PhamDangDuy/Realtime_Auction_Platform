package function;


import service.brain.WalletManager;

import models.Transaction;

import java.sql.SQLException;

/*
Strategy Pattern Design--------------->
Lớp thực thi nhiệm vụ giao dịch thông qua cơ chế của mẫu Strategy
Mỗi Transaction style sẽ có cách hoàn thành giao dịch là khác nhau
vì thế cần một giao diện cung cho việc thực thi tất cả để dễ quản lí
 */
public interface TransactionExcutor {
    void excute(Transaction transaction, WalletManager walletManager) throws SQLException,NullPointerException,IllegalArgumentException;
}
