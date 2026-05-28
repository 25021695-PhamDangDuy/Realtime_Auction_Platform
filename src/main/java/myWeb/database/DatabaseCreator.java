package myWeb.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseCreator{
    private final String url = "jdbc:sqlite:./database/RAP.db";

    public void createDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(url);
    }
}
