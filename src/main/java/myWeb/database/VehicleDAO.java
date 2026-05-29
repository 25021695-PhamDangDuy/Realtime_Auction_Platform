package myWeb.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import myWeb.models.Vehicle;
import myWeb.models.User;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VehicleDAO extends ItemDAOImpl<Vehicle> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected Vehicle mapResultSetToItem(ResultSet rs) throws SQLException {
        UUID itemId = UUID.fromString(rs.getString("ID"));
        String name = rs.getString("Name");
        String condition = rs.getString("Condition");
        long price = rs.getLong("Price");

        // Lấy owner từ owner_ID
        UUID ownerId = UUID.fromString(rs.getString("owner_ID"));
        getUserDAO userDAO = new getUserDAO();
        User owner = userDAO.get(ownerId);

        return new Vehicle(owner, name, price, condition);
    }

    @Override
    public void save(Vehicle vehicle) {
        // Save vào bảng items trước
        super.save(vehicle);
        System.out.println("Vehicle saved successfully");
    }

    @Override
    public void update(Vehicle vehicle) {
        // Update items table
        super.update(vehicle);

    }
}

