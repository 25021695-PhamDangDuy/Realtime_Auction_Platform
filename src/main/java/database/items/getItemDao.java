package database.items;

import models.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class getItemDao extends ItemDAOImpl<Item>{
    @Override
    protected Item mapResultSetToItem(ResultSet rs) throws SQLException {
        UUID itemId = UUID.fromString(rs.getString("ID"));
        String name = rs.getString("Name");
        String condition = rs.getString("Condition");
        long price = rs.getLong("Price");

        ItemDAOImpl<? extends Item> itemDAO = determine(itemId);

        Item item = itemDAO.get(itemId);

        return item;
    }


    //Sử dụng wildcard để nhận diện bất kì đối tượng nào extend từ Item
    private  ItemDAOImpl<? extends Item> determine(UUID ID) throws SQLException{
        String eSQL = "SELECT EXISTS(SELECT 1 FROM electronic_item WHERE ID = ?)";
        String aSQL = "SELECT EXISTS(SELECT 1 FROM art_item WHERE ID = ?)";

        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt1 = conn.prepareStatement(eSQL);
            PreparedStatement psmt2 = conn.prepareStatement(aSQL);

            psmt1.setString(1, gson.toJson(ID));
            psmt2.setString(1,gson.toJson(ID));

            ResultSet elecRs = psmt1.executeQuery();
            ResultSet artRs = psmt2.executeQuery();

            if(elecRs.getBoolean(1)){
                return new VehicleDAO();
            }
            if(artRs.getBoolean(1)){
                return new ArtDAO();
            }
            return new VehicleDAO();
        }
    }


}
