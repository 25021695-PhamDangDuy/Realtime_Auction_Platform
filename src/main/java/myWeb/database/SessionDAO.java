package myWeb.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import myWeb.function.SessionStatus;
import myWeb.models.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SessionDAO implements DataAccessObject<AuctionSession> {
    protected DatabaseCreator databaseCreator = DatabaseCreator.getInstance();
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void save(AuctionSession session) {
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
            psmt.setString(8, session.getStatus().toString());

            psmt.executeUpdate();
            System.out.println("AuctionSession saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving auction session: " + e.getMessage());
        }
    }

    @Override
    public void update(AuctionSession session) {
        String updateSQL = "UPDATE sessions SET currentPrice = ?, status = ?, endTime = ? WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(updateSQL);

            String idString = session.getID().toString();

            psmt.setLong(1, session.getCurrentPrice());
            psmt.setString(2, session.getStatus().toString());
            psmt.setString(3, gson.toJson(session.getEndTime()));
            psmt.setString(4, idString);

            psmt.executeUpdate();
            System.out.println("AuctionSession updated successfully");
        } catch (SQLException e) {
            System.out.println("Error updating auction session: " + e.getMessage());
        }
    }

    @Override
    public AuctionSession get(UUID ID) {
        String querySQL = "SELECT * FROM sessions WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, ID.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSession(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting auction session: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<AuctionSession> getAll() {
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
            System.out.println("Error getting all auction sessions: " + e.getMessage());
        }
        return sessions;
    }

    /**
     * Lấy danh sách người quan sát của một phiên đấu giá
     */
    public Set<User> getObserver(AuctionSession session) {
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
            System.out.println("Error getting observers: " + e.getMessage());
        }
        return observers;
    }

    /**
     * Lấy danh sách phiên đấu giá mà một người dùng đang quan sát
     */
    public List<AuctionSession> getSessionsToWatcher(UUID watcherID) {
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
            System.out.println("Error getting sessions for watcher: " + e.getMessage());
        }
        return sessions;
    }

    /**
     * Lấy danh sách phiên đấu giá đang hoạt động
     */
    public List<AuctionSession> getActiveSessions() {
        List<AuctionSession> sessions = new ArrayList<>();
        String querySQL = "SELECT * FROM sessions WHERE status = 'ACTIVE'";
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
            System.out.println("Error getting active sessions: " + e.getMessage());
        }
        return sessions;
    }

    /**
     * Lấy danh sách phiên đấu giá của một người bán
     */
    public List<AuctionSession> getSessionsBySeller(UUID sellerID) {
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
            System.out.println("Error getting Seller sessions: " + e.getMessage());
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
        SessionStatus status = gson.fromJson(rs.getString("status"),SessionStatus.class);

        // Lấy Seller object
        SellerDAOImpl sellerDAO = new SellerDAOImpl();
        Seller seller = sellerDAO.get(sellerId);
        getItemDAO getItemDAO = new getItemDAO();
        Item item = getItemDAO.get(itemId);
        if (seller != null) {
            BidTicketDAO dao = new BidTicketDAO();
            List<BidTicket> bidTickets = dao.getBySession(sessionId);
            BidHistory bidHistory = new BidHistory(bidTickets);
            return new AuctionSession(sessionId, item, seller, currentPrice, minIncrement, startTime, endTime, status, bidHistory);
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






