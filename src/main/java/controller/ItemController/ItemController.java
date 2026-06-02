package controller.ItemController;

import database.SessionDAO;
import database.getUserDAO;
import database.items.getItemDao;
import function.ItemStatus;
import models.*;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;


public class ItemController {
    private static getUserDAO getUserDAO = new getUserDAO();
    private static getItemDao getItemDao = new getItemDao();
    private static SessionDAO sessionDAO = new SessionDAO();

    public static List<Item> getAllItemByOwnername(String name) throws SQLException {  //Lấy toàn bộ Item;
        User user = getUserDAO.getbyUsername(name);
        List<Item> list =  getItemDao.getbyOwnerID(user.getID());
        return list;
    }

    public static List<Item> getAll_SOLD_byOwnername(String name) throws SQLException{
        User user = getUserDAO.getbyUsername(name);
        List<Item> list = getItemDao.getItembyStatusOwner(user.getID(), ItemStatus.SOLD);
        return list;
    }
    public static List<Item> getAll_AVAILABLE_byOwnername(String name) throws SQLException{
        User user = getUserDAO.getbyUsername(name);
        List<Item> list = getItemDao.getItembyStatusOwner(user.getID(), ItemStatus.AVAILABLE);
        return list;
    }
    public static List<Item> getAll_AUCTION_byOwnername(String name) throws SQLException{
        User user = getUserDAO.getbyUsername(name);
        List<Item> list = getItemDao.getItembyStatusOwner(user.getID(), ItemStatus.AUCTIONING);
        return list;
    }

    public static Item getBySession(UUID sessionID) throws SQLException{
        Item item = getItemDao.getbySessionID(sessionID);

        return item;
    }
    public static Item getByUUID(UUID uuid) throws SQLException {
        Item item= getItemDao.get(uuid);
        return item;
    }


}
