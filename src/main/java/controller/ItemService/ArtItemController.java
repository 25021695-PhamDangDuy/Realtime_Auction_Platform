package controller.ItemService;

import database.getUserDAO;
import database.items.ArtDAO;
import function.ItemStatus;
import models.Art;

import models.Item;
import models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtItemController extends ItemController {
    private static ArtDAO artDAO = new ArtDAO();
    private static getUserDAO getUserDAO = new getUserDAO();
    public static Item createItem(User owner, String name, long price, String condition, String author, String material) {
        return new Art(owner, name, price, condition, author, material);
    }

    public static void saveItem(Item item) throws SQLException {
        artDAO.save((Art)item);
    }
    public static List<Art> get_SOLD_ByOwnername(String name) throws SQLException {
        User u = getUserDAO.getbyUsername(name);

        List<Art> setlist = artDAO.getItembyStatusOwner(u.getID(), ItemStatus.SOLD);
        List<Art> list = new ArrayList<>(setlist);
        return list;
    }
    public static List<Art> get_AVAILABLE_ByOwnername(String name) throws SQLException {
        User u = getUserDAO.getbyUsername(name);

        List<Art> setlist = artDAO.getItembyStatusOwner(u.getID(), ItemStatus.AVAILABLE);
        List<Art> list = new ArrayList<>(setlist);
        return list;
    }
    public static List<Art> getByOwnername(String name) throws SQLException {
        User u = getUserDAO.getbyUsername(name);
        List<Art> setlist = artDAO.getbyOwnerID(u.getID());
        List<Art> list = new ArrayList<>(setlist);
        return list;
    }


}
