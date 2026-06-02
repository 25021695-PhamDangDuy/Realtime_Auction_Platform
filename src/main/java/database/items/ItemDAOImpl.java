package database.items;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import database.DatabaseCreator;
import database.getUserDAO;
import function.ItemStatus;
import models.Electronics;
import models.Item;
import models.User;

import java.sql.*;
import java.util.*;

public abstract class ItemDAOImpl<T extends Item> implements ItemDAO<T> {
    protected DatabaseCreator databaseCreator = DatabaseCreator.getInstance();
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void save(T item) throws SQLException{
        String insertSQL = "INSERT INTO items(ID, owner_ID, Name, Price, Condition, Status) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(insertSQL);

            String status = gson.toJson(item.getItemStatus());

            psmt.setString(1, item.getID().toString());
            psmt.setString(2, item.getOwner().getID().toString());
            psmt.setString(3, item.getName());
            psmt.setLong(4, item.getPrice());
            psmt.setString(5, item.getCondition());
            psmt.setString(6, status);

            psmt.executeUpdate();
            System.out.println("Item saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving item: " + e.getMessage());
        }
    }

    @Override
    public void update(T item) throws SQLException {
        String updateSQL = "UPDATE items SET Name = ?, Price = ?, Condition = ?, Status = ?, owner_ID = ? WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(updateSQL);

            String status = gson.toJson(item.getItemStatus());

            psmt.setLong(2, item.getPrice());
            psmt.setString(1, item.getName());
            psmt.setString(3, item.getCondition());
            psmt.setString(4, status);
            psmt.setString(5, item.getOwner().getID().toString());
            psmt.setString(6, item.getID().toString());

            psmt.executeUpdate();
            System.out.println("Item updated successfully");
        } catch (SQLException e) {
            System.out.println("Error updating item: " + e.getMessage());
        }
    }

    @Override
    public T get(UUID ID) throws SQLException {
        String querySQL = "SELECT * FROM items WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, ID.toString());

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
    public List<T> getbyOwnerID(UUID owner_ID) {
        List<T> items = new ArrayList<>();
        String querySQL = "SELECT * FROM items WHERE owner_ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, owner_ID.toString());

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting items by owner: " + e.getMessage());
        }
        return items;
    }
    public T getbySessionID(UUID session_ID){
        String querySQL = "SELECT item_ID FROM sessions WHERE ID = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, session_ID.toString());

            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                UUID ID =  UUID.fromString(rs.getString("item_ID"));
                return get(ID);
            }
        } catch (SQLException e) {
            System.out.println("Error getting items by owner: " + e.getMessage());
        }
        return null;
    }


    public Set<T> getItemsByStatus(ItemStatus status) {
        Set<T> items = new HashSet<>();
        String querySQL = "SELECT * FROM items WHERE Status = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);
            psmt.setString(1, gson.toJson(status));

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting items by status: " + e.getMessage());
        }
        return items;
    }

    public List<T> getItembyStatusOwner(UUID ownerID, ItemStatus itemStatus){
        List<T> items = new ArrayList<>();
        String querySQL = "SELECT * FROM items WHERE owner_ID = ? AND  Status = ?";
        try (Connection conn = databaseCreator.getConnection()) {
            PreparedStatement psmt = conn.prepareStatement(querySQL);

            psmt.setString(1, ownerID.toString());
            psmt.setString(2, gson.toJson(itemStatus));

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting items by status and owner: " + e.getMessage());
        }
        return items;
    }

    //  method - được override trong các subclass để tạo đúng loại Item
    protected abstract T mapResultSetToItem(ResultSet rs) throws SQLException;

}