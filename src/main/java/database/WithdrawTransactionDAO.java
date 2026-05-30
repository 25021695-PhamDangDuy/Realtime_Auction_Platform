package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import function.TransactionStatus;
import function.TransactionType;

import models.Wallet;
import models.WithdrawTransaction;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WithdrawTransactionDAO implements TransactionDAO<WithdrawTransaction> {
    protected DatabaseCreator databaseCreator = DatabaseCreator.getInstance();
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public WithdrawTransaction getBySenderID(UUID senderID) {
        String querySQL = "SELECT * FROM transactions WHERE sender_ID = ? AND type = ? ORDER BY timestamp DESC LIMIT 1";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, senderID.toString());
            psmt.setString(2, TransactionType.WITHDRAW_WALLET.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToWithdrawTransaction(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting withdraw transaction by sender: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void update(WithdrawTransaction withdrawTransaction) {
        String updateSQL = "UPDATE transactions SET amount = ?, timestamp = ?, status = ? WHERE ID = ? AND type = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(updateSQL);
            psmt.setLong(1, withdrawTransaction.getAmount());
            psmt.setString(2, gson.toJson(withdrawTransaction.getTimestamp()));
            psmt.setString(3, withdrawTransaction.getTransactionStatus().toString());
            psmt.setString(4, withdrawTransaction.getID().toString());
            psmt.setString(5, TransactionType.WITHDRAW_WALLET.toString());

            psmt.executeUpdate();
            System.out.println("WithdrawTransaction updated successfully");
        } catch (SQLException e) {
            System.out.println("Error updating withdraw transaction: " + e.getMessage());
        }
    }

    @Override
    public void save(WithdrawTransaction withdrawTransaction) {
        String insertSQL = "INSERT INTO transactions(ID, sender_ID, receiver_ID, session_ID, amount, timestamp, type, status) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(insertSQL);
            psmt.setString(1, withdrawTransaction.getID().toString());
            psmt.setString(2, withdrawTransaction.getSenderID().toString());
            psmt.setNull(3, Types.VARCHAR);
            psmt.setNull(4, Types.VARCHAR);
            psmt.setLong(5, withdrawTransaction.getAmount());
            psmt.setString(6, gson.toJson(withdrawTransaction.getTimestamp()));
            psmt.setString(7, TransactionType.WITHDRAW_WALLET.toString());
            psmt.setString(8, withdrawTransaction.getTransactionStatus().toString());

            psmt.executeUpdate();
            System.out.println("WithdrawTransaction saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving withdraw transaction: " + e.getMessage());
        }
    }

    @Override
    public WithdrawTransaction get(UUID ID) {
        String querySQL = "SELECT * FROM transactions WHERE ID = ? AND type = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, ID.toString());
            psmt.setString(2, TransactionType.WITHDRAW_WALLET.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToWithdrawTransaction(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting withdraw transaction: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<WithdrawTransaction> getAll() {
        List<WithdrawTransaction> transactions = new ArrayList<>();
        String querySQL = "SELECT * FROM transactions WHERE type = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, TransactionType.WITHDRAW_WALLET.toString());

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToWithdrawTransaction(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all withdraw transactions: " + e.getMessage());
        }
        return transactions;
    }

    private WithdrawTransaction mapResultSetToWithdrawTransaction(ResultSet rs) throws SQLException {
        UUID transactionID = UUID.fromString(rs.getString("ID"));
        UUID senderID = UUID.fromString(rs.getString("sender_ID"));
        long amount = rs.getLong("amount");
        LocalDateTime timestamp = gson.fromJson(rs.getString("timestamp"), LocalDateTime.class);
        TransactionStatus status = TransactionStatus.valueOf(rs.getString("status"));

        WalletDAO walletDAO = new WalletDAO();
        Wallet wallet = walletDAO.getByOwnerID(senderID);
        UUID senderWalletID = wallet != null ? wallet.getID() : null;

        return new WithdrawTransaction(transactionID, amount, senderWalletID, senderID, timestamp, status);
    }
}
