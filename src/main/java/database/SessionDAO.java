package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.BidHistory;
import function.SessionStatus;

import function.SystemLogger;
import models.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import database.items.*;

public class SessionDAO implements DataAccessObject<AuctionSession> {
    protected DatabaseCreator databaseCreator = DatabaseCreator.getInstance();
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private SystemLogger log = SystemLogger.getInstance();

    @Override
    public void save(AuctionSession session) throws SQLException {
        String insertSQL = "INSERT INTO sessions(ID, item_ID, seller_ID, currentPrice, minIncrement, startTime, endTime, status) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(insertSQL);

            String idString = session.getID().toString();
            String itemIdString = session.getItem().getID().toString();
            String sellerIdString = session.getSeller().getID().toString();

            psmt.setString(1, idString);
            psmt.setString(2, itemIdString);
            psmt.setString(3, sellerIdString);
            psmt.setLong(4, session.getCurrentPrice());
            psmt.setLong(5, session.getMinIncrement());
            psmt.setString(6, gson.toJson(session.getStartTime()));
            psmt.setString(7, gson.toJson(session.getEndTime()));
            psmt.setString(8, session.getStatus().name());

            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Lưu thông tin phiên đấu giá|FAILED|" + e.getMessage());
        }
    }

    @Override
    public void update(AuctionSession session) throws SQLException{
        String updateSQL = "UPDATE sessions SET item_ID = ?, seller_ID = ?, currentPrice = ?, status = ?, endTime = ? WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(updateSQL);

            String idString = session.getID().toString();

            psmt.setString(1, session.getItem().getID().toString());
            psmt.setString(2, session.getSeller().getID().toString());
            psmt.setLong(3, session.getCurrentPrice());
            psmt.setString(4, session.getStatus().name());
            psmt.setString(5, gson.toJson(session.getEndTime()));
            psmt.setString(6, idString);

            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Cập nhập thông tin phiên đấu giá|FAILED|" + e.getMessage());
        }
    }

    @Override
    public AuctionSession get(UUID ID) throws SQLException {
        String querySQL = "SELECT * FROM sessions WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, ID.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSession(rs);
            }
        } catch (SQLException e) {
            throw new SQLException("Lấy thông tin phiên đấu giá|FAILED|" + e.getMessage());
        }
        return null;
    }

    @Override
    public List<AuctionSession> getAll() throws SQLException {
        List<AuctionSession> sessions = new ArrayList<>();
        String querySQL = "SELECT * FROM sessions";
        try (Connection conn = databaseCreator.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(querySQL);

            while (rs.next()) {
                AuctionSession session = mapResultSetToSession(rs);
                if (session != null) {
                    sessions.add(session);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Lấy tất cả thông tin các phiên đấu giá|FAILED|" + e.getMessage());
        }
        return sessions;
    }

    /**
     * Lấy danh sách người quan sát của một phiên đấu giá
     */
    public Set<User> getObserver(AuctionSession session) throws SQLException{
        Set<User> observers = new HashSet<>();
        String querySQL = "SELECT DISTINCT u.* FROM users u " +
                "JOIN observers_sessions os ON u.ID = os.user_ID " +
                "WHERE os.sessions_ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, session.getID().toString());

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                User observer = mapResultSetToUser(rs);
                if (observer != null) {
                    observers.add(observer);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Lấy thông tin người theo dõi phiên ID:" + session.getID() + "|FAILED|" + e.getMessage());
        }
        return observers;
    }

    /**
     * Lấy danh sách phiên đấu giá mà một người dùng đang quan sát
     */
    public List<AuctionSession> getSessionsToWatcher(UUID watcherID) throws SQLException{
        List<AuctionSession> sessions = new ArrayList<>();
        String querySQL = "SELECT DISTINCT s.* FROM sessions s " +
                "JOIN observers_sessions os ON s.ID = os.sessions_ID " +
                "WHERE os.user_ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, watcherID.toString());

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                AuctionSession session = mapResultSetToSession(rs);
                if (session != null) {
                    sessions.add(session);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Lấy thông tin phiên đấu giá đang theo dõi của userID: " + watcherID.toString() +"|FAILED|" + e.getMessage());

        }
        return sessions;
    }

    /**
     * Lấy danh sách phiên đấu giá đang hoạt động
     */
    public List<AuctionSession> getActiveSessions() throws SQLException{
        String status = SessionStatus.RUNNING.name();
        List<AuctionSession> sessions = new ArrayList<>();
        String querySQL = "SELECT * FROM sessions WHERE status = ?";

        // 🔍 DEBUG
        System.out.println("DEBUG - Searching for status: [" + status + "]");
        System.out.println("DEBUG - SessionStatus.RUNNING enum: " + SessionStatus.RUNNING);

        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(querySQL);
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            // 🔍 Debug - kiểm tra xem có kết quả không
            if (!rs.next()) {
                System.out.println("DEBUG - No results found!");
                // Kiểm tra xem có bản ghi nào trong sessions không
                Statement testStmt = conn.createStatement();
                ResultSet testRs = testStmt.executeQuery("SELECT status FROM sessions LIMIT 5");
                System.out.println("DEBUG - Actual statuses in DB:");
                while (testRs.next()) {
                    System.out.println("  - [" + testRs.getString("status") + "]");
                }
            } else {
                // Nếu có kết quả, process bình thường
                do {
                    AuctionSession session = mapResultSetToSession(rs);
                    if (session != null) {
                        sessions.add(session);
                    }
                } while (rs.next());
            }
        } catch (SQLException e) {
            throw new SQLException("Lưu thông tin phiên đấu giá đang hoạt động|FAILED|" + e.getMessage());
        }

        System.out.println("DEBUG - Found " + sessions.size() + " active sessions");
        return sessions;
    }
    public List<AuctionSession> getStartingSession() throws SQLException {
        String status = SessionStatus.UPCOMING.name();
        List<AuctionSession> sessions = new ArrayList<>();
        String querySQL = "SELECT * FROM sessions WHERE status = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(querySQL);

            stmt.setString(1,status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AuctionSession session = mapResultSetToSession(rs);
                if (session != null) {
                    sessions.add(session);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Lưu thông tin phiên đấu giá sắp mở|FAILED|" + e.getMessage());
        }
        return sessions;
    }

    /**
     * Lấy danh sách phiên đấu giá của một người bán
     */
    public List<AuctionSession> getSessionsBySeller(UUID sellerID) throws SQLException{
        List<AuctionSession> sessions = new ArrayList<>();
        String querySQL = "SELECT * FROM sessions WHERE seller_ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, sellerID.toString());

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                AuctionSession session = mapResultSetToSession(rs);
                if (session != null) {
                    sessions.add(session);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Lấy thông tin phiên đấu giá của sellerID: " + sellerID.toString() +"|FAILED|" + e.getMessage());
        }
        return sessions;
    }

    /*
    Helper method: Convert ResultSet to AuctionSession
    */
    private AuctionSession mapResultSetToSession(ResultSet rs) throws SQLException {
        UUID sessionId = UUID.fromString(rs.getString("ID"));
        UUID itemId = UUID.fromString(rs.getString("item_ID"));
        UUID sellerId = UUID.fromString(rs.getString("seller_ID"));
        long currentPrice = rs.getLong("currentPrice");
        long minIncrement = rs.getLong("minIncrement");
        LocalDateTime startTime = gson.fromJson(rs.getString("startTime"), LocalDateTime.class);
        LocalDateTime endTime = gson.fromJson(rs.getString("endTime"), LocalDateTime.class);
        SessionStatus status = SessionStatus.valueOf(rs.getString("status"));


        // Lấy Seller object
        SellerDAOImpl sellerDAO = new SellerDAOImpl();
        Seller seller = sellerDAO.get(sellerId);

        getItemDao getItemDAO = new getItemDao();
        Item item = getItemDAO.get(itemId);

        if (seller != null && item != null) {
            return new AuctionSession(sessionId, item, seller, currentPrice, minIncrement, endTime, startTime, status, null);
        }
        return null;
    }

    /**
     * Helper method: Convert ResultSet to User
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        UUID userId = UUID.fromString(rs.getString("ID"));
        String username = rs.getString("Username");
        String password = rs.getString("Password");
        String role = rs.getString("role");
        getUserDAO userDAO = new getUserDAO();
        return userDAO.createUserByRole(userId, username, password, role);
    }
}






