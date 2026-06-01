package controller.ItemController;

import models.Electronics;
import models.Item;
import models.User;

public class ElectronicItemController{
    public static Item createItem(User owner,String name,long price,String condition, Integer month) {
        return new Electronics(owner,name,price,condition,month);
    }
    public static void saveItem(Item item){}
}
