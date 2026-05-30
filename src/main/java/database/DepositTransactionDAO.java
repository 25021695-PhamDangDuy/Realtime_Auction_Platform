package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import function.TransactionStatus;
import function.TransactionType;

import models.DepositTransaction;
import models.Wallet;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DepositTransactionDAO implements TransactionDAO<DepositTransaction> {
    protected DatabaseCreator databaseCreator = DatabaseCreator.getInstance();
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public DepositTransaction getBySenderID(UUID senderID) {
        String querySQL = "SELECT * FROM transactions WHERE sender_ID = ? AND type = ? ORDER BY timestamp DESC LIMIT 1";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, senderID.toString());
            psmt.setString(2, TransactionType.DEPOSIT_WALLET.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDepositTransaction(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting deposit transaction by sender: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void update(DepositTransaction depositTransaction) {
        String updateSQL = "UPDATE transactions SET amount = ?, timestamp = ?, status = ? WHERE ID = ? AND type = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(updateSQL);
            psmt.setLong(1, depositTransaction.getAmount());
            psmt.setString(2, gson.toJson(depositTransaction.getTimestamp()));
            psmt.setString(3, depositTransaction.getTransactionStatus().toString());
            psmt.setString(4, depositTransaction.getID().toString());
            psmt.setString(5, TransactionType.DEPOSIT_WALLET.toString());

            psmt.executeUpdate();
            System.out.println("DepositTransaction updated successfully");
        } catch (SQLException e) {
            System.out.println("Error updating deposit transaction: " + e.getMessage());
        }
    }

    @Override
    public void save(DepositTransaction depositTransaction) {
        String insertSQL = "INSERT INTO transactions(ID, sender_ID, receiver_ID, session_ID, amount, timestamp, type, status) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(insertSQL);
            psmt.setString(1, depositTransaction.getID().toString());
            psmt.setString(2, depositTransaction.getSenderID().toString());
            psmt.setNull(3, Types.VARCHAR);
            psmt.setNull(4, Types.VARCHAR);
            psmt.setLong(5, depositTransaction.getAmount());
            psmt.setString(6, gson.toJson(depositTransaction.getTimestamp()));
            psmt.setString(7, TransactionType.DEPOSIT_WALLET.toString());
            psmt.setString(8, depositTransaction.getTransactionStatus().toString());

            psmt.executeUpdate();
            System.out.println("DepositTransaction saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving deposit transaction: " + e.getMessage());
        }
    }

    @Override
    public DepositTransaction get(UUID ID) {
        String querySQL = "SELECT * FROM transactions WHERE ID = ? AND type = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, ID.toString());
            psmt.setString(2, TransactionType.DEPOSIT_WALLET.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDepositTransaction(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting deposit transaction: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<DepositTransaction> getAll() {
        List<DepositTransaction> transactions = new ArrayList<>();
        String querySQL = "SELECT * FROM transactions WHERE type = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, TransactionType.DEPOSIT_WALLET.toString());

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToDepositTransaction(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all deposit transactions: " + e.getMessage());
        }
        return transactions;
    }

    private DepositTransaction mapResultSetToDepositTransaction(ResultSet rs) throws SQLException {
        UUID transactionID = UUID.fromString(rs.getString("ID"));
        UUID senderID = UUID.fromString(rs.getString("sender_ID"));
        long amount = rs.getLong("amount");
        LocalDateTime timestamp = gson.fromJson(rs.getString("timestamp"), LocalDateTime.class);
        TransactionStatus status = TransactionStatus.valueOf(rs.getString("status"));

        WalletDAO walletDAO = new WalletDAO();
        Wallet wallet = walletDAO.getByOwnerID(senderID);
        UUID senderWalletID = wallet != null ? wallet.getID() : null;

        return new DepositTransaction(transactionID, amount, senderWalletID, senderID, timestamp, status);
    }
}
