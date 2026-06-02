package database.items;

import function.ItemStatus;
import models.Art;
import models.Electronics;
import models.Item;
import models.Vehicle;

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
        ItemStatus status = gson.fromJson(rs.getString("Status"),ItemStatus.class);

        ItemDAOImpl<? extends Item> itemDAO = determine(itemId);

        return itemDAO.get(itemId);
    }


    //Sử dụng wildcard để nhận diện bất kì đối tượng nào extend từ Item
    private  ItemDAOImpl<? extends Item> determine(UUID ID) throws SQLException{
        String eSQL = "SELECT EXISTS(SELECT 1 FROM electronic_item WHERE ID = ?)";
        String aSQL = "SELECT EXISTS(SELECT 1 FROM art_item WHERE ID = ?)";

        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt1 = conn.prepareStatement(eSQL);
            PreparedStatement psmt2 = conn.prepareStatement(aSQL);

            psmt1.setString(1, ID.toString());
            psmt2.setString(1,ID.toString());

            ResultSet elecRs = psmt1.executeQuery();
            ResultSet artRs = psmt2.executeQuery();

            if( elecRs.next() && elecRs.getBoolean(1)){
                return new ElectronicDAO();
            }
            if( artRs.next() && artRs.getBoolean(1)){
                return new ArtDAO();
            }
            return new VehicleDAO();
        }
    }

    @Override
    public void update(Item item) throws SQLException {
        UUID ID = item.getID();

        if(item instanceof Art){
            ArtDAO itemDAO = new ArtDAO();
            itemDAO.update((Art) item);
        }else if(item instanceof Electronics){
            ElectronicDAO electronicDAO = new ElectronicDAO();
            electronicDAO.update((Electronics) item);
        }else if(item instanceof Vehicle){
            VehicleDAO vehicleDAO = new VehicleDAO();
            vehicleDAO.update((Vehicle) item);
        }else {
            throw new IllegalArgumentException("Update không thành công");
        }
    }
}
