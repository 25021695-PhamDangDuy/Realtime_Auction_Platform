package database;

import models.Item;
import java.util.Set;
import java.util.UUID;

public interface ItemDAO<T extends Item> extends DataAccessObject<T>{
    //Getter infor
    Set<T> getbyOwnerID(UUID owner_ID);


}
