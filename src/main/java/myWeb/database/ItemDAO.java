package myWeb.database;

import myWeb.models.Item;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ItemDAO<T extends Item> extends DataAccessObject<T>{
    //Getter infor
    Set<T> getbyOwnerID(UUID owner_ID);


}
