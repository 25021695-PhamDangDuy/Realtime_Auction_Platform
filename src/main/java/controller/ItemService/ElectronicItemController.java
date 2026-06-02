package controller.ItemService;

import database.getUserDAO;
import database.items.ElectronicDAO;
import function.ItemStatus;
import models.Electronics;
import models.Item;
import models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ElectronicItemController extends ItemController{
    private static ElectronicDAO electronicDAO = new ElectronicDAO();
    private static getUserDAO getUserDAO = new getUserDAO();
    public static Item createItem(User owner,String name,long price,String condition, Integer month) {
        return new Electronics(owner,name,price,condition,month);
    }
    public static void saveItem(Item item) throws SQLException {
        electronicDAO.save((Electronics) item);
    }
    public static List<Electronics> getByOwnername(String name) throws SQLException {
        User u = getUserDAO.getbyUsername(name);
        List<Electronics> setlist = electronicDAO.getbyOwnerID(u.getID());
        return new ArrayList<>(setlist);
    }
    public static List<Electronics> get_AVAILABLE_ByOwnername(String name) throws SQLException {
        User u = getUserDAO.getbyUsername(name);

        List<Electronics> setlist = electronicDAO.getItembyStatusOwner(u.getID(), ItemStatus.AVAILABLE);
        return new ArrayList<>(setlist);
    }
    public static List<Electronics> get_SOLD_ByOwnername(String name) throws SQLException {
        User u = getUserDAO.getbyUsername(name);

        List<Electronics> setlist = electronicDAO.getItembyStatusOwner(u.getID(), ItemStatus.SOLD);
        return new ArrayList<>(setlist);
    }
}
