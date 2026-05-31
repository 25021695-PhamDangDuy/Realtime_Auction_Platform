package DataBase;

import models.Item;
import java.util.Set;
import java.util.UUID;

public interface ItemDAO<T extends Item> extends database.DataAccessObject<T> {
    //Getter infor
    Set<T> getbyOwnerID(UUID owner_ID);


}
