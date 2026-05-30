package database;

import models.Item;
import java.sql.ResultSet;
import java.sql.SQLException;

public class getItemDAO extends ItemDAOImpl<Item>{
    @Override
    protected Item mapResultSetToItem(ResultSet rs) throws SQLException {
        return null;
    }
}
