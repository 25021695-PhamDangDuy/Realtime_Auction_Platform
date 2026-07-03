package Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import service.brain.AccountController;
import service.brain.AuctionManager;
import service.brain.WalletManager;
import database.SellerDAOImpl;
import database.items.VehicleDAO;
import models.*;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class Main implements Application {
    public static void main(String[] args) throws SQLException {


    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load()
    }
}