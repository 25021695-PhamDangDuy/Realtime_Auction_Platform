package database;

import database.DatabaseCreator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.Item;
import java.sql.*;
import java.util.*;

public abstract class ItemDAOImpl<T extends Item> implements database.ItemDAO<T> {
    protected DatabaseCreator databaseCreator = DatabaseCreator.getInstance();
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void save(T item) {
        String insertSQL = "INSERT INTO items(ID, owner_ID, Name, Price, Condition, Status) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(insertSQL);

            String idGson = gson.toJson(item.getID());
            String ownerIdGson = gson.toJson(item.getOwner().getID());

            psmt.setString(1, idGson);
            psmt.setString(2, ownerIdGson);
            psmt.setString(3, item.getName());
            psmt.setLong(4, item.getPrice());
            psmt.setString(5, item.getCondition());
            psmt.setString(6, item.getItemStatus().toString());

            psmt.executeUpdate();
            System.out.println("Item saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving item: " + e.getMessage());
        }
    }

    @Override
    public void update(T item) {
        String updateSQL = "UPDATE items SET Name = ?, Price = ?, Condition = ?, Status = ?, owner_ID = ? WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(updateSQL);

            String idGson = gson.toJson(item.getID());
            String ownerIdGson = gson.toJson(item.getOwner().getID());

            psmt.setString(1, item.getName());
            psmt.setLong(2, item.getPrice());
            psmt.setString(3, item.getCondition());
            psmt.setString(4, item.getItemStatus().toString());
            psmt.setString(5, ownerIdGson);
            psmt.setString(6, idGson);

            psmt.executeUpdate();
            System.out.println("Item updated successfully");
        } catch (SQLException e) {
            System.out.println("Error updating item: " + e.getMessage());
        }
    }

    @Override
    public T get(UUID ID) {
        String querySQL = "SELECT * FROM items WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, gson.toJson(ID));

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToItem(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting item: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<T> getAll() {
        List<T> items = new ArrayList<>();
        String querySQL = "SELECT * FROM items";
        try (Connection conn = databaseCreator.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(querySQL);

            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all items: " + e.getMessage());
        }
        return items;
    }

    @Override
    public Set<T> getbyOwnerID(UUID owner_ID) {
        Set<T> items = new HashSet<>();
        String querySQL = "SELECT * FROM items WHERE owner_ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, gson.toJson(owner_ID));

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting items by owner: " + e.getMessage());
        }
        return items;
    }


    public Set<T> getItemsByStatus(String status) {
        Set<T> items = new HashSet<>();
        String querySQL = "SELECT * FROM items WHERE Status = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, status);

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting items by status: " + e.getMessage());
        }
        return items;
    }

    // Abstract method - được override trong các subclass để tạo đúng loại Item
    protected abstract T mapResultSetToItem(ResultSet rs) throws SQLException;
}