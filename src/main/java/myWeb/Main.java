package myWeb;

import myWeb.database.DatabaseCreator;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DatabaseCreator databaseCreator = DatabaseCreator.getInstance();
        databaseCreator.getConnection();

    }
}