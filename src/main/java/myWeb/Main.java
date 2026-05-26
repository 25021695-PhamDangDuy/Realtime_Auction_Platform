package myWeb;

import myWeb.controller.AuctionManager;
import myWeb.function.SessionChecker;
import myWeb.models.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

//public class Main {
//    public static void main(String[] args) {
//        System.out.println("System starting");
//        AuctionManager manager = AuctionManager.getInstance();
//        Runnable watcher = new AuctionSessionWatcher();
//        SessionChecker sessionChecker = new SessionChecker();
//
//        Seller seller1 = new Seller("001","Duy","000");
//        Vehicle toyota = new Vehicle(seller1,"T00","ToyotaCar",20000d,"New");
//        LocalDateTime end = LocalDateTime.parse("2026-05-26T00:34:00");
//
//        manager.createSession(toyota.getId(),toyota,seller1,toyota.getPrice(),200,end);
//        AuctionSession session1 = manager.getSession(toyota.getId());
//
//        Bidder bidder1 = new Bidder("002","DuyHack","000");
//        Bidder bidder2 = new Bidder("003","Long","000");
//        Runnable runnable1 = () -> {
//            while(sessionChecker.isAuctioning(session1)){
//                System.out.println("Thread 1: Starting placeBid");
//                double price = session1.getCurrentPrice() + session1.getMinIncrement();
//                try{
//                    session1.placeBid(bidder1,price);
//                    System.out.println("Successfull placeBid");
//                    System.out.println("Now price is: " + session1.getCurrentPrice() + "And winner is me: " + session1.getTopBidder().getName());
//                    System.out.println("Stopping!");
//                } catch (IllegalArgumentException e) {
//                    System.out.println(e.getMessage());
//                }finally {
//                    System.out.println("Finished");
//                }
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        };
//        Runnable runnable2 = () -> {
//            Scanner sc = new Scanner(System.in);
//            while(sessionChecker.isAuctioning(session1)){
//                System.out.println("You:");
//                Double number = Double.parseDouble(sc.nextLine());
//
//                try{
//                    session1.placeBid(bidder2,number);
//                    System.out.println("Successfull placeBid");
//                    System.out.println("Now price is: " + session1.getCurrentPrice() + "And winner is me: " + session1.getTopBidder().getName());
//                    System.out.println("Stopping!");
//                } catch (IllegalArgumentException e) {
//                    System.out.println(e.getMessage());
//                }finally {
//                    System.out.println("Finished");
//                }
//            }
//
//        };
//
//        System.out.println("Start Watcher");
//        Thread thread1 = new Thread(watcher);
//        thread1.start();
//        System.out.println("Thread starting");
//        Thread thread2 = new Thread(runnable1);
//        Thread thread3 = new Thread(runnable2);
//        thread3.start();
//        thread2.start();



    public class Main {
        static class ThreadSafeCounter {
            private int count = 0;
            synchronized void increment() { count++; }
            int get() { return count; }
        }

        public static void main(String[] args) throws InterruptedException {
            System.out.println("=== SYSTEM STARTING - MULTI-THREAD STRESS TEST ===\n");

            AuctionManager manager = AuctionManager.getInstance();
            SessionChecker sessionChecker = new SessionChecker();

            // Setup Auction
            Seller seller1 = new Seller("001", "Duy", "000");
            Vehicle toyota = new Vehicle(seller1, "T00", "ToyotaCar", 20000d, "New");
            LocalDateTime end = LocalDateTime.parse("2026-05-26T00:49:00");

            manager.createSession(toyota.getId(), toyota, seller1, toyota.getPrice(), 200, end);
            AuctionSession session1 = manager.getSession(toyota.getId());

            // Bidders
            Bidder[] bidders = {
                    new Bidder("002", "DuyHack", "000"),
                    new Bidder("003", "Long", "000"),
                    new Bidder("004", "Minh", "000"),
                    new Bidder("005", "Hoa", "000")
            };

            ThreadSafeCounter successBids = new ThreadSafeCounter();
            ThreadSafeCounter failedBids = new ThreadSafeCounter();
            List<String> inconsistencies = Collections.synchronizedList(new LinkedList<>());

            // ===== SCENARIO 1: Rapid Sequential Bids =====
            System.out.println("[SCENARIO 1] Rapid Sequential Bids - 10 Threads bidding continuously");
            ExecutorService executor = Executors.newFixedThreadPool(10);

            for (int i = 0; i < 10; i++) {
                final int threadId = i;
                final Bidder currentBidder = bidders[i % bidders.length];

                executor.submit(() -> {
                    for (int j = 0; j < 5; j++) {
                        try {
                            double newPrice = session1.getCurrentPrice() + session1.getMinIncrement();
                            session1.placeBid(currentBidder, newPrice);
                            successBids.increment();

                            // Check consistency
                            if (newPrice != session1.getCurrentPrice()) {
                                inconsistencies.add(String.format(
                                        "T%d: Expected price %.2f, got %.2f",
                                        threadId, newPrice, session1.getCurrentPrice()
                                ));
                            }
                            if (!session1.getTopBidder().equals(currentBidder)) {
                                inconsistencies.add(String.format(
                                        "T%d: Bid placed but not top bidder!", threadId
                                ));
                            }

                            System.out.printf("[T%d-Bid%d] ✓ Price: %.2f | Leader: %s%n",
                                    threadId, j, session1.getCurrentPrice(), session1.getTopBidder().getName());

                        } catch (IllegalArgumentException e) {
                            failedBids.increment();
                            System.out.printf("[T%d-Bid%d] ✗ %s%n", threadId, j, e.getMessage());
                        }

                        try { Thread.sleep(50 + (int)(Math.random() * 100)); }
                        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);

            System.out.println("\n[SCENARIO 1 RESULTS]");
            System.out.println("Success Bids: " + successBids.get());
            System.out.println("Failed Bids: " + failedBids.get());
            System.out.println("Final Price: " + session1.getCurrentPrice());
            System.out.println("Final Winner: " + session1.getTopBidder().getName());

            // ===== SCENARIO 2: Concurrent Read/Write Stress =====
            System.out.println("\n[SCENARIO 2] Concurrent Read/Write Stress Test");

            executor = Executors.newFixedThreadPool(15);
            CountDownLatch startSignal = new CountDownLatch(1);

            // 5 Bid threads
            for (int i = 0; i < 5; i++) {
                final int threadId = i + 100;
                final Bidder bidder = bidders[i % bidders.length];

                executor.submit(() -> {
                    try {
                        startSignal.await();
                        for (int k = 0; k < 3; k++) {
                            double price = session1.getCurrentPrice() + session1.getMinIncrement();
                            session1.placeBid(bidder, price);
                            successBids.increment();
                        }
                    } catch (Exception e) {
                        failedBids.increment();
                    }
                });
            }

            // 10 Reader threads (checking state)
            for (int i = 0; i < 10; i++) {
                final int threadId = i + 200;

                executor.submit(() -> {
                    try {
                        startSignal.await();
                        for (int k = 0; k < 5; k++) {
                            double price = session1.getCurrentPrice();
                            Bidder leader = session1.getTopBidder();

                            // Validate: price should not be negative
                            if (price < 0) {
                                inconsistencies.add(String.format(
                                        "T%d: Negative price detected: %.2f", threadId, price
                                ));
                            }
                            if (leader == null) {
                                inconsistencies.add(String.format("T%d: Null leader!", threadId));
                            }

                            System.out.printf("[T%d-Read] Price: %.2f | Leader: %s%n",
                                    threadId, price, leader != null ? leader.getName() : "NULL");

                            Thread.sleep(10);
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                });
            }

            Thread.sleep(500);
            startSignal.countDown();

            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);

            System.out.println("\n[SCENARIO 2 RESULTS]");
            System.out.println("Final Price: " + session1.getCurrentPrice());
            System.out.println("Final Winner: " + session1.getTopBidder().getName());

            // ===== SCENARIO 3: Edge Case - Minimum

}
}
