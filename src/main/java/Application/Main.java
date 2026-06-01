package Application;

import controller.AccountController;
import controller.WalletManager;
import database.BidderDAOImpl;
import database.SellerDAOImpl;
import function.DiversityRule;
import models.Bidder;
import models.Seller;
import models.User;
import models.Wallet;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DiversityRule diversityRule = new DiversityRule();
        System.out.println(diversityRule.validate("1109asdSd"));
    }
}