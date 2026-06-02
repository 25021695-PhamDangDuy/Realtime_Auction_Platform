package database.items;

import database.getUserDAO;
import function.ItemStatus;
import models.Electronics;
import models.Item;
import models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ElectronicDAO extends ItemDAOImpl<Electronics> {

    @Override
    protected Electronics mapResultSetToItem(ResultSet rs) throws SQLException {
        UUID itemId = UUID.fromString(rs.getString("ID"));
        String name = rs.getString("Name");
        String condition = rs.getString("Condition");
        long price = rs.getLong("Price");
        ItemStatus status = gson.fromJson(rs.getString("Status"),ItemStatus.class);
        // Lấy owner từ owner_ID
        UUID ownerId = UUID.fromString(rs.getString("owner_ID"));
        getUserDAO a = new getUserDAO();
        User owner = a.get(ownerId);

        // Lấy thông tin Electronics từ bảng electronic_item
        Integer monthWarranty = 0;
        String electronicQuerySQL = "SELECT HSD FROM electronic_item WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(electronicQuerySQL);
            psmt.setString(1, itemId.toString());
            ResultSet elecRs = psmt.executeQuery();
            if (elecRs.next()) {
                monthWarranty = elecRs.getInt("HSD");
            }
        }

        return new Electronics(itemId,owner, name, price, condition, monthWarranty,status);
    }

    @Override
    public void save(Electronics electronics) throws SQLException {
        // Save vào bảng items trước
        super.save(electronics);

        // Sau đó save vào bảng electronic_item
        String insertElecSQL = "INSERT INTO electronic_item(ID, HSD) VALUES(?, ?)";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(insertElecSQL);

            String idGson = electronics.getID().toString();
            psmt.setString(1, idGson);
            psmt.setInt(2, electronics.getMonthofWarranty());

            psmt.executeUpdate();
            System.out.println("Electronics saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving electronics: " + e.getMessage());
        }
    }

    @Override
    public void update(Electronics electronics) throws SQLException {
        // Update items table
        super.update(electronics);

        // Update electronic_item table
        String updateElecSQL = "UPDATE electronic_item SET HSD = ? WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(updateElecSQL);

            String idGson = electronics.getID().toString();
            psmt.setString(2, idGson);
            psmt.setInt(1, electronics.getMonthofWarranty());

            psmt.executeUpdate();
            System.out.println("Electronics updated successfully");
        } catch (SQLException e) {
            System.out.println("Error updating electronics:" + e.getMessage());
        }
    }
}