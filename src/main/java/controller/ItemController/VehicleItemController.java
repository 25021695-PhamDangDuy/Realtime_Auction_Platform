package controller.ItemController;


import models.Vehicle;
import models.Item;
import models.User;

public class VehicleItemController {

    public static Item createItem(User owner, String name, long price, String condition) {
        return new Vehicle(owner, name, price, condition);
    }

    public static void saveItem(Item item) {
        // Logic lưu vào Database sau này viết ở đây
    }
}
