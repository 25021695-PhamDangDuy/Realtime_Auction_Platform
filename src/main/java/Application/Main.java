package Application;

import controller.ItemService.ArtItemController;
import controller.ItemService.ElectronicItemController;
import controller.ItemService.ItemController;
import controller.ItemService.VehicleItemController;
import controller.brain.AccountController;
import controller.brain.AuctionManager;
import database.SellerDAOImpl;
import database.SessionDAO;
import database.getUserDAO;
import database.items.ArtDAO;
import function.ItemStatus;
import models.*;
import database.items.getItemDao;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        AuctionManager auctionManager = AuctionManager.getInstance();

        AccountController accountController = AccountController.getInstance();

        User u = accountController.getInfor("DUYPHAMm");
        System.out.println(u instanceof Seller);
    }
}