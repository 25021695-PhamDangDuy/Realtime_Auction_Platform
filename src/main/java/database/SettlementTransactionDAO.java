package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import function.SystemLogger;
import function.TransactionStatus;
import function.TransactionType;

import models.AuctionSession;
import models.SettlementTransaction;
import models.Wallet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SettlementTransactionDAO implements TransactionDAO<SettlementTransaction> {
    protected DatabaseCreator databaseCreator = DatabaseCreator.getInstance();
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private SystemLogger log = SystemLogger.getInstance();

    @Override
    public SettlementTransaction getBySenderID(UUID senderID) {
        String querySQL = "SELECT * FROM transactions WHERE sender_ID = ? AND type = ? ORDER BY timestamp DESC LIMIT 1";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, senderID.toString());
            psmt.setString(2, TransactionType.AUCTION_SETTLEMENT.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSettlementTransaction(rs);
            }
        } catch (SQLException e) {
            System.out.println(" getting settlement transaction by sender: " + e.getMessage());
        }
        return null;
    }

    public SettlementTransaction getByReceiverID(UUID receiverID) {
        String querySQL = "SELECT * FROM transactions WHERE receiver_ID = ? AND type = ? ORDER BY timestamp DESC LIMIT 1";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, receiverID.toString());
            psmt.setString(2, TransactionType.AUCTION_SETTLEMENT.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSettlementTransaction(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting settlement transaction by receiver: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void update(SettlementTransaction settlementTransaction) {
        String updateSQL = "UPDATE transactions SET amount = ?, timestamp = ?, status = ? WHERE ID = ? AND type = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(updateSQL);
            psmt.setLong(1, settlementTransaction.getAmount());
            psmt.setString(2, gson.toJson(settlementTransaction.getTimestamp()));
            psmt.setString(3, settlementTransaction.getTransactionStatus().toString());
            psmt.setString(4, settlementTransaction.getID().toString());
            psmt.setString(5, TransactionType.AUCTION_SETTLEMENT.toString());

            psmt.executeUpdate();
            System.out.println("SettlementTransaction updated successfully");
        } catch (SQLException e) {
            System.out.println("Error updating settlement transaction: " + e.getMessage());
        }
    }

    @Override
    public void save(SettlementTransaction settlementTransaction) {
        String insertSQL = "INSERT INTO transactions(ID, sender_ID, receiver_ID, session_ID, amount, timestamp, type, status) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(insertSQL);
            psmt.setString(1, settlementTransaction.getID().toString());
            psmt.setString(2, settlementTransaction.getSenderID().toString());
            psmt.setString(3, settlementTransaction.getReceiverID().toString());
            psmt.setString(4, settlementTransaction.getSession().getID().toString());
            psmt.setLong(5, settlementTransaction.getAmount());
            psmt.setString(6, gson.toJson(settlementTransaction.getTimestamp()));
            psmt.setString(7, TransactionType.AUCTION_SETTLEMENT.toString());
            psmt.setString(8, settlementTransaction.getTransactionStatus().toString());

            psmt.executeUpdate();
            System.out.println("SettlementTransaction saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving settlement transaction: " + e.getMessage());
        }
    }

    @Override
    public SettlementTransaction get(UUID ID) {
        String querySQL = "SELECT * FROM transactions WHERE ID = ? AND type = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, ID.toString());
            psmt.setString(2, TransactionType.AUCTION_SETTLEMENT.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSettlementTransaction(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting settlement transaction: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<SettlementTransaction> getAll() {
        List<SettlementTransaction> transactions = new ArrayList<>();
        String querySQL = "SELECT * FROM transactions WHERE type = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, TransactionType.AUCTION_SETTLEMENT.toString());

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToSettlementTransaction(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all settlement transactions: " + e.getMessage());
        }
        return transactions;
    }

    private SettlementTransaction mapResultSetToSettlementTransaction(ResultSet rs) throws SQLException {
        UUID transactionID = UUID.fromString(rs.getString("ID"));
        UUID senderID = UUID.fromString(rs.getString("sender_ID"));
        UUID receiverID = UUID.fromString(rs.getString("receiver_ID"));
        UUID sessionID = UUID.fromString(rs.getString("session_ID"));
        long amount = rs.getLong("amount");
        LocalDateTime timestamp = gson.fromJson(rs.getString("timestamp"), LocalDateTime.class);
        TransactionStatus status = TransactionStatus.valueOf(rs.getString("status"));

        WalletDAO walletDAO = new WalletDAO();
        Wallet senderWallet = walletDAO.getByOwnerID(senderID);
        Wallet receiverWallet = walletDAO.getByOwnerID(receiverID);
        UUID senderWalletID = senderWallet != null ? senderWallet.getID() : null;
        UUID receiverWalletID = receiverWallet != null ? receiverWallet.getID() : null;

        SessionDAO sessionDAO = new SessionDAO();
        AuctionSession session = sessionDAO.get(sessionID);

        return new SettlementTransaction(transactionID, amount, senderWalletID, senderID, timestamp, status,
                session, receiverWalletID, receiverID);
    }
}
