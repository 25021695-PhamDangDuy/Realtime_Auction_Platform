package Application;

import controller.ItemService.ArtItemController;
import controller.ItemService.ElectronicItemController;
import controller.ItemService.ItemController;
import controller.ItemService.VehicleItemController;
import controller.brain.AccountController;
import controller.brain.AuctionManager;
import controller.brain.WalletManager;
import database.SellerDAOImpl;
import database.SessionDAO;
import database.getUserDAO;
import database.items.ArtDAO;
import database.items.VehicleDAO;
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

        Seller s = new Seller("SELL","0000");
        SellerDAOImpl sellerDAO = new SellerDAOImpl();
        sellerDAO.save(s);

        WalletManager.getInstance().createWallet(s.getID(),1000);

        Vehicle v = new Vehicle(s,"CAR",9799,"GOOD");
        VehicleDAO vehicleDAO = new VehicleDAO();
        vehicleDAO.save(v);

        auctionManager.createSession(v,s,v.getPrice(),100,LocalDateTime.now().plusMinutes(10));

        System.out.println(auctionManager.getSessionActive());


    }
}