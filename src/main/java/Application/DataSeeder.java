package Application;
import database.*;
import database.items.*;
import function.SessionStatus;
import models.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class DataSeeder {

    public static void main(String[] args) {
        try {
            System.out.println("=== Bắt đầu Seeding Dữ Liệu ===\n");
            seedDatabase();
            System.out.println("\n=== Seeding Thành Công ===");
        } catch (Exception e) {
            System.err.println("Lỗi Seeding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void seedDatabase() throws SQLException {
        // Tạo users
        System.out.println("--- Tạo Users ---");
        Seller seller1 = createAndSaveSeller("seller1", "pass123");
        Seller seller2 = createAndSaveSeller("seller2", "pass123");
        Bidder bidder1 = createAndSaveBidder("bidder1", "pass123");
        Bidder bidder2 = createAndSaveBidder("bidder2", "pass123");
        Bidder bidder3 = createAndSaveBidder("bidder3", "pass123");

        // Tạo items (cần owner để tạo item)
        System.out.println("\n--- Tạo Items ---");
        Electronics item1 = createAndSaveElectronics(seller1, "Laptop Dell XPS", 500000L, "Mới", 24);
        Electronics item2 = createAndSaveElectronics(seller1, "iPhone 15 Pro", 800000L, "Chưa sử dụng", 12);
        Electronics item3 = createAndSaveElectronics(seller2, "Samsung 65\" TV", 1200000L, "Như mới", 36);
        Electronics item4 = createAndSaveElectronics(seller2, "Sony WH-1000XM5", 180000L, "Chưa sử dụng", 12);

        // Tạo Auction Sessions
        System.out.println("\n--- Tạo Auction Sessions ---");
        createAndSaveAuctionSession(
                item1, seller1, 400000L, 50000L,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(35),
                SessionStatus.UPCOMING
        );

        createAndSaveAuctionSession(
                item2, seller1, 700000L, 100000L,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().plusMinutes(30),
                SessionStatus.RUNNING
        );

        createAndSaveAuctionSession(
                item3, seller2, 1000000L, 200000L,
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now().minusMinutes(5),
                SessionStatus.FINISHED
        );

        createAndSaveAuctionSession(
                item4, seller2, 150000L, 25000L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(1).plusMinutes(30),
                SessionStatus.UPCOMING
        );

        System.out.println("\n✓ Tất cả dữ liệu đã được tạo thành công!");
        displaySummary(seller1, seller2, bidder1, bidder2, bidder3);
    }

    private static Seller createAndSaveSeller(String username, String password) throws SQLException {
        UUID sellerId = UUID.randomUUID();
        Seller seller = new Seller(sellerId, username, password);

        SellerDAOImpl sellerDAO = new SellerDAOImpl();
        sellerDAO.save(seller);

        Wallet wallet = new Wallet(sellerId, 10000000L);
        WalletDAO walletDAO = new WalletDAO();
        walletDAO.save(wallet);

        seller.addWallet(wallet);
        System.out.println("✓ Seller tạo: " + username + " (ID: " + sellerId.toString().substring(0, 8) + "...)");
        return seller;
    }

    private static Bidder createAndSaveBidder(String username, String password) throws SQLException {
        UUID bidderId = UUID.randomUUID();
        Bidder bidder = new Bidder(bidderId, username, password);

        BidderDAOImpl bidderDAO = new BidderDAOImpl();
        bidderDAO.save(bidder);

        Wallet wallet = new Wallet(bidderId, 5000000L);
        WalletDAO walletDAO = new WalletDAO();
        walletDAO.save(wallet);

        bidder.addWallet(wallet);
        System.out.println("✓ Bidder tạo: " + username + " (ID: " + bidderId.toString().substring(0, 8) + "...)");
        return bidder;
    }


    private static Electronics createAndSaveElectronics(Seller owner, String name, long price, String condition, int monthWarranty) throws SQLException {
        UUID itemId = UUID.randomUUID();
        Electronics item = new Electronics(itemId, owner, name, price, condition, monthWarranty, function.ItemStatus.AVAILABLE);

        ElectronicDAO itemDAO = new ElectronicDAO();
        itemDAO.save(item);

        System.out.println("✓ Item tạo: " + name + " - " + price + "đ");
        return item;
    }

    private static void createAndSaveAuctionSession(
            Item item, Seller seller, long startPrice, long minIncrement,
            LocalDateTime startTime, LocalDateTime endTime, SessionStatus status
    ) throws SQLException {
        UUID sessionId = UUID.randomUUID();
        AuctionSession session = new AuctionSession(
                item, seller, startPrice, minIncrement, endTime, startTime, status
        );

        SessionDAO sessionDAO = new SessionDAO();
        sessionDAO.save(session);

        System.out.println("✓ Phiên đấu giá tạo: " + item.getName() + " - Status: " + status.getDescription());
    }

    private static void displaySummary(Seller s1, Seller s2, Bidder b1, Bidder b2, Bidder b3) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TÓMO TẮT DỮ LIỆU SEED");
        System.out.println("=".repeat(60));
        System.out.println("\n📦 USERS:");
        System.out.println("  Sellers: 2 (seller1, seller2)");
        System.out.println("  Bidders: 3 (bidder1, bidder2, bidder3)");
        System.out.println("\n💰 WALLETS (SỐ DƯ BAN ĐẦU):");
        System.out.println("  Seller: 10,000,000đ (mỗi người)");
        System.out.println("  Bidder: 5,000,000đ (mỗi người)");
        System.out.println("  Admin: 0đ");
        System.out.println("\n🎁 ITEMS:");
        System.out.println("  Electronics: 4 chiếc");
        System.out.println("    - 2 từ seller1");
        System.out.println("    - 2 từ seller2");
        System.out.println("\n🏪 AUCTION SESSIONS:");
        System.out.println("  UPCOMING: 2 phiên");
        System.out.println("  RUNNING: 1 phiên");
        System.out.println("  FINISHED: 1 phiên");
        System.out.println("\n" + "=".repeat(60));
    }
}