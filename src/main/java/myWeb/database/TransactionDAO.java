package myWeb.database;

import myWeb.models.Transaction;

import java.util.UUID;

public interface TransactionDAO<T extends Transaction> extends DataAccessObject<T> {
    T getBySenderID(UUID senderID);
}
