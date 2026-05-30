package database;

import models.Bidder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BidderDAOImpl extends UserDAOImpl<Bidder>{


    @Override
    public Bidder get(UUID ID) {
        String retrieveSQL = "SELECT * FROM users WHERE ID = ? AND role = 'BIDDER'";  //  Kiểm tra role
        Bidder bidder = null;
        try(Connection conn = databaseCreator.getConnection()){
            PreparedStatement prsmt = conn.prepareStatement(retrieveSQL);
            prsmt.setString(1, ID.toString());
            
            ResultSet rs = prsmt.executeQuery();

            if(rs.next()){
                // Kiểm tra role để đảm bảo đây thực sự là Bidder
                String role = rs.getString("role");
                if (!"BIDDER".equals(role)) {
                    System.out.println("❌ Lỗi: ID này là " + role + ", không phải BIDDER!");
                    return null;
                }
                
                UUID id = UUID.fromString(rs.getString("ID"));
                String name = rs.getString("Username");
                String pw = rs.getString("Password");

                bidder = new Bidder(id, name, pw);
            }
        }catch (SQLException e){
            System.out.println("Không tìm thấy Bidder: " + e.getMessage());
        }
        return bidder;
    }

    @Override
    public List<Bidder> getAll() {
        String retrieveSQL = "SELECT * FROM users WHERE role = 'BIDDER'";  //  Chỉ lấy Bidder
        List<Bidder> bidderList = new ArrayList<>();
        try(Connection conn = databaseCreator.getConnection()){
            PreparedStatement prsmt = conn.prepareStatement(retrieveSQL);
            ResultSet rs = prsmt.executeQuery();

            while(rs.next()){
                UUID id = UUID.fromString(rs.getString("ID"));
                String name = rs.getString("Username");
                String pw = rs.getString("Password");

                Bidder bidder = new Bidder(id, name, pw);
                bidderList.add(bidder);
            }
        }catch (SQLException e){
            System.out.println("Không tìm thấy Bidders: " + e.getMessage());
        }
        return bidderList;
    }
}
