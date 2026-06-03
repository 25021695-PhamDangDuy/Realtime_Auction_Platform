package database;

import controller.AuctionObserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import database.DatabaseCreator;

public class ObserverDAO implements DataAccessObject<AuctionObserver> {
    private Connection connection;

    public ObserverDAO() throws SQLException {
        this.connection = DatabaseCreator.getInstance().getConnection();
    }

    @Override
    public void update(AuctionObserver auctionObserver) throws SQLException {
        // Observer là interface, không cần update vào DB
    }

    @Override
    public void save(AuctionObserver auctionObserver) throws SQLException {
        // Observer là interface, cần lưu user_ID và session_ID vào observers_sessions table
    }

    @Override
    public AuctionObserver get(UUID ID) throws SQLException {
        return null;
    }

    @Override
    public List<AuctionObserver> getAll() throws SQLException {
        return List.of();
    }

    /**
     * Lấy danh sách observer theo session ID
     * @param sessionID UUID của phiên đấu giá
     * @return Danh sách user_ID những người theo dõi phiên
     */
    public List<AuctionObserver> getObserversBySessionID(UUID sessionID) throws SQLException {
        List<AuctionObserver> observers = new ArrayList<>();
        String sql = "SELECT user_ID FROM observers_sessions WHERE sessions_ID = ?";
        getUserDAO getUserDAO = new getUserDAO();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sessionID.toString());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                AuctionObserver a = (AuctionObserver) getUserDAO.get(UUID.fromString(resultSet.getString("user_ID")));
                observers.add(a);
            }
        }

        return observers;
    }

    /**
     * Thêm observer vào session
     * @param userID UUID của user
     * @param sessionID UUID của phiên đấu giá
     */
    public void addObserverToSession(UUID userID, UUID sessionID) throws SQLException {
        String sql = "INSERT INTO observers_sessions (user_ID, sessions_ID) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userID.toString());
            statement.setString(2, sessionID.toString());
            statement.executeUpdate();
        }
    }

    /**
     * Xóa observer khỏi session
     * @param userID UUID của user
     * @param sessionID UUID của phiên đấu giá
     */
    public void removeObserverFromSession(UUID userID, UUID sessionID) throws SQLException {
        String sql = "DELETE FROM observers_sessions WHERE user_ID = ? AND sessions_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userID.toString());
            statement.setString(2, sessionID.toString());
            statement.executeUpdate();
        }
    }

    /**
     * Kiểm tra user có theo dõi session này không
     * @param userID UUID của user
     * @param sessionID UUID của phiên đấu giá
     * @return true nếu user đang theo dõi session
     */
    public boolean isObserverOfSession(UUID userID, UUID sessionID) throws SQLException {
        String sql = "SELECT 1 FROM observers_sessions WHERE user_ID = ? AND sessions_ID = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userID.toString());
            statement.setString(2, sessionID.toString());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    /**
     * Lấy số lượng observer của một session
     * @param sessionID UUID của phiên đấu giá
     * @return Số lượng người theo dõi
     */
    public int getObserverCountBySessionID(UUID sessionID) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM observers_sessions WHERE sessions_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sessionID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        }

        return 0;
    }

    /**
     * Xóa tất cả observer của một session (khi session kết thúc)
     * @param sessionID UUID của phiên đấu giá
     */
    public void clearObserversForSession(UUID sessionID) throws SQLException {
        String sql = "DELETE FROM observers_sessions WHERE sessions_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sessionID.toString());
            statement.executeUpdate();
        }
    }

    /**
     * Lấy danh sách các session ID mà một user cụ thể đang theo dõi
     * @param userID UUID của người dùng (observer)
     * @return Danh sách sessions_ID các phiên đấu giá mà user đang theo dõi
     */
    public List<UUID> getSessionsByObserverID(UUID userID) throws SQLException {
        List<UUID> sessions = new ArrayList<>();
        String sql = "SELECT sessions_ID FROM observers_sessions WHERE user_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userID.toString());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                sessions.add(UUID.fromString(resultSet.getString("sessions_ID")));
            }
        }

        return sessions;
    }
}