package service.ItemService;


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
    private static final VehicleDAO vehicleDAO = new VehicleDAO();
    private static final getUserDAO getUserDAO = new getUserDAO();
    public static Item createItem(User owner, String name, long price, String condition) {
        return new Vehicle(owner, name, price, condition);
    }

    public static void saveItem(Item item) throws SQLException {
        vehicleDAO.save((Vehicle) item);
    }

    public static List<Vehicle> get_SOLD_ByOwnername(String name) throws SQLException {
        User u = getUserDAO.getbyUsername(name);

        List<Vehicle> setlist = vehicleDAO.getItembyStatusOwner(u.getID(), ItemStatus.SOLD);
        return new ArrayList<>(setlist);
    }
    public static List<Vehicle> get_AVAILABLE_ByOwnername(String name) throws SQLException {
        User u = getUserDAO.getbyUsername(name);

        List<Vehicle> setlist = vehicleDAO.getItembyStatusOwner(u.getID(), ItemStatus.AVAILABLE);
        return new ArrayList<>(setlist);
    }

    public static List<Vehicle> getByOwnername(String name) throws SQLException {
        User u = getUserDAO.getbyUsername(name);
        List<Vehicle> setlist = vehicleDAO.getbyOwnerID(u.getID());
        return new ArrayList<>(setlist);
    }
}
