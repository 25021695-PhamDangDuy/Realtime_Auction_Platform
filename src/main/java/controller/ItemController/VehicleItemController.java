package controller.ItemController;


import database.getUserDAO;
import database.items.VehicleDAO;
import function.ItemStatus;
import models.Vehicle;
import models.Item;
import models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleItemController extends ItemController {
    private static VehicleDAO vehicleDAO = new VehicleDAO();
    private static getUserDAO getUserDAO = new getUserDAO();
    public static Item createItem(User owner, String name, long price, String condition) {
        return new Vehicle(owner, name, price, condition);
    }

    public static void saveItem(Item item) {
        vehicleDAO.save((Vehicle) item);
    }

    public static List<Vehicle> get_SOLD_ByOwnername(String name) throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        User u = getUserDAO.getbyUsername(name);

        List<Vehicle> setlist = vehicleDAO.getItembyStatusOwner(u.getID(), ItemStatus.SOLD);
        list.addAll(setlist);
        return list;
    }
    public static List<Vehicle> get_AVAILABLE_ByOwnername(String name) throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        User u = getUserDAO.getbyUsername(name);

        List<Vehicle> setlist = vehicleDAO.getItembyStatusOwner(u.getID(), ItemStatus.AVAILABLE);
        list.addAll(setlist);
        return list;
    }

    public static List<Vehicle> getByOwnername(String name) throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        User u = getUserDAO.getbyUsername(name);
        List<Vehicle> setlist = vehicleDAO.getbyOwnerID(u.getID());
        list.addAll(setlist);
        return list;
    }
}
