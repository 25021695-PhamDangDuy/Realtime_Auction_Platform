package database.items;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import database.getUserDAO;
import function.ItemStatus;
import models.User;
import models.Vehicle;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class VehicleDAO extends ItemDAOImpl<Vehicle> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected Vehicle mapResultSetToItem(ResultSet rs) throws SQLException {
        UUID itemId = UUID.fromString(rs.getString("ID"));
        String name = rs.getString("Name");
        String condition = rs.getString("Condition");
        long price = rs.getLong("Price");
        ItemStatus status = gson.fromJson(rs.getString("Status"),ItemStatus.class);
        // Lấy owner từ owner_ID
        UUID ownerId = UUID.fromString(rs.getString("owner_ID"));
        getUserDAO userDAO = new getUserDAO();
        User owner = userDAO.get(ownerId);

        return new Vehicle(itemId,owner, name, price, condition,status);
    }

    @Override
    public void save(Vehicle vehicle) throws SQLException {
        // Save vào bảng items trước
        super.save(vehicle);
        System.out.println("Vehicle saved successfully");
    }

    @Override
    public void update(Vehicle vehicle) throws SQLException {
        // Update items table
        super.update(vehicle);

    }
}

