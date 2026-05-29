package myWeb.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import myWeb.models.Wallet;

import java.sql.*;
import java.util.*;
import java.util.UUID;

public class WalletDAO implements DataAccessObject<Wallet> {
    protected DatabaseCreator databaseCreator = DatabaseCreator.getInstance();
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void save(Wallet wallet) {
        String insertSQL = "INSERT INTO wallet(ID, owner_ID, balance, balance_locked) VALUES(?, ?, ?, ?)";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(insertSQL);

            String idString = wallet.getID().toString();
            String ownerIdString = wallet.getOwnerID().toString();

            psmt.setString(1, idString);
            psmt.setString(2, ownerIdString);
            psmt.setLong(3, wallet.getBalance());
            psmt.setLong(4, wallet.getBalanceLocked());

            psmt.executeUpdate();
            System.out.println("Wallet saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving wallet: " + e.getMessage());
        }
    }

    @Override
    public void update(Wallet wallet) {
        String updateSQL = "UPDATE wallet SET balance = ?, balance_locked = ? WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(updateSQL);

            String idString = wallet.getID().toString();

            psmt.setLong(1, wallet.getBalance());
            psmt.setLong(2, wallet.getBalanceLocked());
            psmt.setString(3, idString);

            psmt.executeUpdate();
            System.out.println("Wallet updated successfully");
        } catch (SQLException e) {
            System.out.println("Error updating wallet: " + e.getMessage());
        }
    }

    @Override
    public Wallet get(UUID ID) {
        String querySQL = "SELECT * FROM wallet WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, ID.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToWallet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting wallet: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Wallet> getAll() {
        List<Wallet> wallets = new ArrayList<>();
        String querySQL = "SELECT * FROM wallet";
        try (Connection conn = databaseCreator.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(querySQL);

            while (rs.next()) {
                wallets.add(mapResultSetToWallet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all wallets: " + e.getMessage());
        }
        return wallets;
    }

    /**
     * Lấy ví của một người dùng theo owner_ID
     */
    public Wallet getByOwnerID(UUID owner_ID) {
        String querySQL = "SELECT * FROM wallet WHERE owner_ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, owner_ID.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToWallet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting wallet by owner ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy danh sách ví có số dư tự do lớn hơn một giá trị nào đó
     */
    public List<Wallet> getWalletsByMinimumBalance(long minBalance) {
        List<Wallet> wallets = new ArrayList<>();
        String querySQL = "SELECT * FROM wallet WHERE balance >= ?";
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
        String querySQL = "SELECT * FROM wallet WHERE balance_locked > 0";
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
        long balance = rs.getLong("balance");
        long balanceLocked = rs.getLong("balance_locked");

        Wallet wallet = new Wallet(ownerId, balance,balanceLocked);
        return wallet;
    }

}