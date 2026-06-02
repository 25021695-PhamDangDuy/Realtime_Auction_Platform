package Application;

import controller.brain.AccountController;
import function.DiversityRule;
import database.items.*;
import database.getUserDAO;
import models.Art;
import models.Seller;
import models.User;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        AccountController accountController = AccountController.getInstance();
        ArtDAO artDAO = new ArtDAO();
        getUserDAO getUserDAO = new getUserDAO();

        User u = accountController.getInfor("Linh");
        System.out.println(u);
    }
}