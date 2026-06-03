package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import controller.brain.WalletManager;
import function.SystemLogger;
import models.*;
import server.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

//User data access object
public abstract class UserDAOImpl<T extends User> implements UserDAO<T>{
    protected DatabaseCreator databaseCreator = DatabaseCreator.getInstance();
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private SystemLogger log = SystemLogger.getInstance();
    //Lớp phục vụ việc truy xuất thông tin của user

    public void save(T user){
        String insertSQL = "INSERT INTO users(ID, Username, Password, role) VALUES(?, ?, ?, ?)";
        try(Connection conn = databaseCreator.getConnection()){
            PreparedStatement preparedStatement = conn.prepareStatement(insertSQL);

            String idString = user.getID().toString();
            String name = user.getName();
            String pw = user.getPassword();
            String role = determineRole(user);  // ← Xác định role

            preparedStatement.setString(1, idString);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, pw);
            preparedStatement.setString(4, role);

            preparedStatement.executeUpdate();
            System.out.println("Save thành công ID: " +  idString + " với role: " + role);
        }catch (SQLException e){
            System.out.println("Lỗi save(User): " + e.getMessage());
        }
    }

    public void update(T user){
        String updateSQL = "UPDATE users SET Username = ?, Password = ?, role = ? WHERE ID = ?";
        try(Connection conn = databaseCreator.getConnection()){
            PreparedStatement preparedStatement = conn.prepareStatement(updateSQL);

            String idString = user.getID().toString();
            String role = determineRole(user);

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, role);
            preparedStatement.setString(4, idString);

            preparedStatement.executeUpdate();
            System.out.println("Update thành công, role: " + role);
        }catch (SQLException e){
            System.out.println("Lỗi update(User): " + e.getMessage());
        }
    }

    //  Helper method: Xác định role dựa trên class type
    protected String determineRole(User user) {
        if (user instanceof Seller) {
            return "SELLER";
        } else if (user instanceof Admin) {
            return "ADMIN";
        } else if (user instanceof Bidder) {
            return "BIDDER";
        }
        return "UNKNOWN";
    }


    // Helper method: Tạo User object tương ứng dựa trên role
    public User createUserByRole(UUID id, String username, String password, String role) throws SQLException, IllegalArgumentException {
        switch(role.toUpperCase().trim()) {  // ← Chuyển thành UPPERCASE
            case "BIDDER":
                Bidder b = new Bidder(id, username, password);
                return b;
            case "SELLER":
                return new Seller(id, username, password);
            case "ADMIN":
                return new Admin(id, username, password);
            default:
                return null;
        }
    }

}
