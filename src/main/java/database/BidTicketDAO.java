package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import function.BidStatus;
import models.AuctionSession;
import models.BidTicket;
import models.Bidder;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BidTicketDAO implements DataAccessObject<BidTicket> {
    protected DatabaseCreator databaseCreator = DatabaseCreator.getInstance();
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void update(BidTicket bidTicket) {
        String updateSQL = "UPDATE bidTickets SET amount = ?, status = ?, timestamp = ? WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(updateSQL);

            String idString = bidTicket.getID().toString();

            psmt.setLong(1, bidTicket.getAmount());
            psmt.setString(2, gson.toJson(bidTicket.getStatus()));
            psmt.setString(3, gson.toJson(bidTicket.getTimeBid()));
            psmt.setString(4, idString);

            psmt.executeUpdate();
            System.out.println("BidTicket updated successfully");
        } catch (SQLException e) {
            System.out.println("Error updating bid ticket: " + e.getMessage());
        }
    }

    @Override
    public void save(BidTicket bidTicket) {
        String insertSQL = "INSERT INTO bidTickets(ID, user_ID, session_ID, timestamp, amount, status) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(insertSQL);

            String idString = bidTicket.getID().toString();
            String userIdString = bidTicket.getBidder().getID().toString();
            String sessionIdString = bidTicket.getSession().getID().toString();

            psmt.setString(1, idString);
            psmt.setString(2, userIdString);
            psmt.setString(3, sessionIdString);
            psmt.setString(4, gson.toJson(bidTicket.getTimeBid()));
            psmt.setLong(5, bidTicket.getAmount());
            psmt.setString(6, gson.toJson(bidTicket.getStatus()));

            psmt.executeUpdate();
            System.out.println("BidTicket saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving bid ticket: " + e.getMessage());
        }
    }

    @Override
    public BidTicket get(UUID ID) {
        String querySQL = "SELECT * FROM bidTickets WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, ID.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBidTicket(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting bid ticket: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<BidTicket> getAll() {
        List<BidTicket> bidTickets = new ArrayList<>();
        String querySQL = "SELECT * FROM bidTickets";
        try (Connection conn = databaseCreator.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(querySQL);

            while (rs.next()) {
                bidTickets.add(mapResultSetToBidTicket(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all bid tickets: " + e.getMessage());
        }
        return bidTickets;
    }

    public List<BidTicket> getByUser(UUID userID) {
        List<BidTicket> bidTickets = new ArrayList<>();
        String querySQL = "SELECT * FROM bidTickets WHERE user_ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, userID.toString());

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                bidTickets.add(mapResultSetToBidTicket(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting bid tickets by user: " + e.getMessage());
        }
        return bidTickets;
    }


    public List<BidTicket> getBySession(UUID sessionID) {
        List<BidTicket> bidTickets = new ArrayList<>();
        String querySQL = "SELECT * FROM bidTickets WHERE session_ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, sessionID.toString());

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                bidTickets.add(mapResultSetToBidTicket(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting bid tickets by session: " + e.getMessage());
        }
        return bidTickets;
    }

    // Helper method: Convert ResultSet to BidTicket
    private BidTicket mapResultSetToBidTicket(ResultSet rs) throws SQLException {
        UUID bidTicketId = UUID.fromString(rs.getString("ID"));
        UUID userId = UUID.fromString(rs.getString("user_ID"));
        UUID sessionId = UUID.fromString(rs.getString("session_ID"));
        LocalDateTime timestamp = gson.fromJson(rs.getString("timestamp"),LocalDateTime.class);
        long amount = rs.getLong("amount");
        BidStatus status = gson.fromJson(rs.getString("status"), BidStatus.class);

        // Lấy User object
        BidderDAOImpl bidderDAO = new BidderDAOImpl();
        Bidder user = bidderDAO.get(userId);
        // Lấy Session object
        SessionDAO sessionDAO = new SessionDAO();
        AuctionSession session = sessionDAO.get(sessionId);

        return new BidTicket(bidTicketId,user,session,timestamp,amount,status);
    }

}
