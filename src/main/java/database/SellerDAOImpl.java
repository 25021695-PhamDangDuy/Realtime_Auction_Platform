package database;

import models.Seller;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SellerDAOImpl extends UserDAOImpl<Seller>{

    @Override
    public Seller get(UUID ID) {
        String retrieveSQL = "SELECT * FROM users WHERE ID = ?";
        Seller seller = null;
        try(Connection conn = databaseCreator.getConnection()){
            PreparedStatement prsmt = conn.prepareStatement(retrieveSQL);

            String id_Gson = gson.toJson(ID);
            ResultSet rs = prsmt.executeQuery();

            if(rs.next()){
                UUID id = UUID.fromString(rs.getString("ID"));
                String name = rs.getString("Name");
                String pw = rs.getString("Password");

                seller = new Seller(id,name,pw);
            }
        }catch (SQLException e){
            System.out.println("Khong tim thay user");
        }
        return seller;
    }

    @Override
    public List<Seller> getAll() {
        String retrieveSQL = "SELECT * FROM users";
        List<Seller> userList = new ArrayList<>();
        try(Connection conn = databaseCreator.getConnection()){
            PreparedStatement prsmt = conn.prepareStatement(retrieveSQL);

            ResultSet rs = prsmt.executeQuery();

            while(rs.next()){
                UUID id = UUID.fromString(rs.getString("ID"));
                String name = rs.getString("Name");
                String pw = rs.getString("Password");

                Seller bidder = new Seller(id,name,pw);
                userList.add(bidder);
            }
        }catch (SQLException e){
            System.out.println("Khong tim thay user");
        }
        return userList;
    }
}
