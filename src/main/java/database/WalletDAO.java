package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import function.SystemLogger;
import models.Wallet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WalletDAO implements DataAccessObject<Wallet> {
    protected DatabaseCreator databaseCreator = DatabaseCreator.getInstance();
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private SystemLogger log = SystemLogger.getInstance();

    @Override
    public void save(Wallet wallet) throws SQLException {
        String insertSQL = "INSERT INTO wallets(ID, owner_ID, Balance, BalanceLocked) VALUES(?, ?, ?, ?)";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(insertSQL);

            String idString = wallet.getID().toString();
            String ownerIdString = wallet.getOwnerID().toString();

            psmt.setString(1, idString);
            psmt.setString(2, ownerIdString);
            psmt.setLong(3, wallet.getBalance());
            psmt.setLong(4, wallet.getBalanceLocked());
            psmt.executeUpdate();
            log.info("Lưu thành công ví: " + wallet.getID().toString() + " của userID: " + wallet.getOwnerID().toString() + "|SUCCESS");
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void update(Wallet wallet) throws SQLException{
        String updateSQL = "UPDATE wallets SET Balance = ?, BalanceLocked = ? WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(updateSQL);

            String idString = wallet.getID().toString();

            psmt.setLong(1, wallet.getBalance());
            psmt.setLong(2, wallet.getBalanceLocked());
            psmt.setString(3, idString);

            psmt.executeUpdate();
            log.info("Cập nhập thành công ví: " + wallet.getID().toString() + " của userID: " + wallet.getOwnerID().toString() + "|SUCCESS");
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Wallet get(UUID ID) throws SQLException {
        String querySQL = "SELECT * FROM wallets WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {

            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, ID.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                log.info("Tiến hành lấy thông tin ví có ID: " + ID + "|SUCCESS");
                return mapResultSetToWallet(rs);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return null;
    }

    @Override
    public List<Wallet> getAll() throws SQLException{
        List<Wallet> wallets = new ArrayList<>();
        String querySQL = "SELECT * FROM wallets";
        try (Connection conn = databaseCreator.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(querySQL);

            while (rs.next()) {
                log.info("Tiến hành lấy thông tin toàn bộ ví hiện có|SUCCESS" );
                wallets.add(mapResultSetToWallet(rs));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return wallets;
    }

    /**
     * Lấy ví của một người dùng theo owner_ID
     */
    public Wallet getByOwnerID(UUID owner_ID) throws SQLException{
        String querySQL = "SELECT * FROM wallets WHERE owner_ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, owner_ID.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                log.info("Tiến hành lấy thông tin ví của userID: " + owner_ID.toString()+ "|SUCCESS" );
                return mapResultSetToWallet(rs);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return null;
    }

    /**
     * Lấy danh sách ví có số dư tự do lớn hơn một giá trị nào đó
     */
    public List<Wallet> getWalletsByMinimumBalance(long minBalance) {
        List<Wallet> wallets = new ArrayList<>();
        String querySQL = "SELECT * FROM wallets WHERE Balance >= ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setLong(1, minBalance);

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                wallets.add(mapResultSetToWallet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting wallets by minimum balance: " + e.getMessage());
        }
        return wallets;
    }

    /**
     * Lấy danh sách ví có số dư bị khóa lớn hơn 0
     */
    public List<Wallet> getWalletsWithLockedBalance() {
        List<Wallet> wallets = new ArrayList<>();
        String querySQL = "SELECT * FROM wallets WHERE BalanceLocked > 0";
        try (Connection conn = databaseCreator.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(querySQL);

            while (rs.next()) {
                wallets.add(mapResultSetToWallet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting wallets with locked balance: " + e.getMessage());
        }
        return wallets;
    }

    /**
     * Helper method: Convert ResultSet thành Wallet object
     */
    private Wallet mapResultSetToWallet(ResultSet rs) throws SQLException {
        UUID walletId = UUID.fromString(rs.getString("ID"));
        UUID ownerId = UUID.fromString(rs.getString("owner_ID"));
        long balance = rs.getLong("Balance");
        long balanceLocked = rs.getLong("BalanceLocked");

        Wallet wallet = new Wallet(walletId, ownerId, balance,balanceLocked);
        return wallet;
    }

    public boolean isHasID(UUID ID){
        String SQL = "SELECT EXISTS(SELECT 1 FROM wallets WHERE ID = ?)";
        try (Connection conn = databaseCreator.getConnection()){
            PreparedStatement psmt = conn.prepareStatement(SQL);
            psmt.setString(1, ID.toString());
            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại của ví", e);
        }
        return false;
    }

    public boolean isHasOwnerID(UUID ID){
        String SQL = "SELECT EXISTS(SELECT 1 FROM users WHERE ID = ?)";
        try (Connection conn = databaseCreator.getConnection()){
            PreparedStatement psmt = conn.prepareStatement(SQL);
            psmt.setString(1, ID.toString());
            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại của ví", e);
        }
        return false;
    }
}