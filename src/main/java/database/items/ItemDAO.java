package database.items;

import database.DataAccessObject;
import models.Item;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ItemDAO<T extends Item> extends DataAccessObject<T> {
    //Getter infor
    List<T> getbyOwnerID(UUID owner_ID);


}
