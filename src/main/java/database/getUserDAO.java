package database;

import models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class getUserDAO extends UserDAOImpl<User> {

    @Override
    public List<User> getAll() {
        return List.of();
    }

    // Method để lấy user theo ID và tự động convert về đúng type
    public User get(UUID userId) {
        String selectSQL = "SELECT * FROM users WHERE ID = ?";
        String id = userId.toString();
        try(Connection conn = databaseCreator.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");
                String username = resultSet.getString("Username");
                String password = resultSet.getString("Password");

                // Tạo user object dựa trên role
                return createUserByRole(userId, username, password, role);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi get(userId): " + e.getMessage());
        }
        return null;
    }

    public User getbyUsername(String name) throws SQLException{
        String SQLquery = "SELECT ID, Username, Password, role FROM users WHERE Username = ?";
        try(Connection conn = databaseCreator.getConnection()){
            PreparedStatement preparedStatement = conn.prepareStatement(SQLquery);

            preparedStatement.setString(1, name);

            ResultSet rs = preparedStatement.executeQuery();
            if(rs == null){
                throw new NullPointerException("Không tìm thấy ID user");
            }
            String ID = rs.getString("ID");
            String Name = rs.getString("Username");
            String Pw = rs.getString("Password");
            String role = rs.getString("role");

            return createUserByRole(UUID.fromString(ID),Name,Pw,role);
        }catch (SQLException e){
            throw new SQLException(e);
        }
    }
}
