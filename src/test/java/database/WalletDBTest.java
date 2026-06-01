import controller.WalletManager;
import database.BidderDAOImpl;
import database.SellerDAOImpl;
import models.Bidder;
import models.Seller;
import models.Wallet;

//package database;
//
//import org.junit.jupiter.api.BeforeAll;
//
//public class WalletDBTest {
//    WalletManager walletManager = WalletManager.getInstance();
//    BidderDAOImpl bidderDAO = new BidderDAOImpl();
//    SellerDAOImpl sellerDAO = new SellerDAOImpl();
//
//    Bidder bidder = new Bidder("Linhcuteee0981290311111","1501");
//    Seller seller = new Seller("PhamDangDuyyyypodawd111111","0000");
//
//        sellerDAO.save(seller);
//        bidderDAO.save(bidder);
//        walletManager.createWallet(bidder.getID(),100000);
//        walletManager.createWallet(seller.getID(),15010000);
//
//    Wallet sellerWallet = walletManager.getWalletbyOwnerID(seller.getID());
//    Wallet bidderWallet = walletManager.getWalletbyOwnerID(bidder.getID());
//
//        walletManager.depositWallet(sellerWallet.getID(),seller.getID(),100000);
//        walletManager.lockMoney(bidderWallet.getID(),bidder.getID(),50000);

//}
