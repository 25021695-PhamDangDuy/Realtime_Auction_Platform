package database.items;

import database.getUserDAO;
import models.Art;
import models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ArtDAO extends ItemDAOImpl<Art> {

    @Override
    protected Art mapResultSetToItem(ResultSet rs) throws SQLException {
        UUID itemId = UUID.fromString(rs.getString("ID").replaceAll("\"", ""));
        String name = rs.getString("Name");
        String condition = rs.getString("Condition");
        long price = rs.getLong("Price");
    
        // Lấy owner từ owner_ID
        UUID ownerId = UUID.fromString(rs.getString("owner_ID"));
        getUserDAO a = new getUserDAO();
        User owner = a.get(ownerId);

        // Lấy thông tin Art từ bảng art_item
        String author = "";
        String material = "";
        String artQuerySQL = "SELECT author, material FROM art_item WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(artQuerySQL);
            psmt.setString(1, gson.toJson(itemId));
            ResultSet artRs = psmt.executeQuery();
            if (artRs.next()) {
                author = artRs.getString("author");
                material = artRs.getString("material");
            }
        }

        return new Art(owner, name, price, condition, author, material);
    }

    @Override
    public void save(Art art) {
        // Save vào bảng items trước
        super.save(art);

        // Sau đó save vào bảng art_item
        String insertArtSQL = "INSERT INTO art_item(ID, author, material) VALUES(?, ?, ?)";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(insertArtSQL);

            String idGson = art.getID().toString();
            psmt.setString(1, idGson);
            psmt.setString(2, art.getAuthor());
            psmt.setString(3, art.getMaterial());

            psmt.executeUpdate();
            System.out.println("Art saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving art: " + e.getMessage());
        }
    }

    @Override
    public void update(Art art) {
        // Update items table
        super.update(art);

        // Update art_item table
        String updateArtSQL = "UPDATE art_item SET author = ?, material = ? WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(updateArtSQL);

            String idGson = art.getID().toString();
            psmt.setString(1, art.getAuthor());
            psmt.setString(2, art.getMaterial());
            psmt.setString(3, idGson);

            psmt.executeUpdate();
            System.out.println("Art updated successfully");
        } catch (SQLException e) {
            System.out.println("Error updating art: " + e.getMessage());
        }
    }




}