package myWeb;

import myWeb.controller.AuctionManager;
import myWeb.controller.AuctionSession;
import myWeb.models.*;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        AuctionManager manager = AuctionManager.getInstance();

        Seller a = new Seller("S001", "Duy","0000");
        Bidder b1 = new Bidder("B001", "Linh", "0000");
        Bidder b2 = new Bidder("B002","Dũng","00000");
        Bidder b3 = new Bidder("B003","Long","0000");

        Vehicle car = new Vehicle("001","Toyota", 2000d,"New", a);
        Electronics electronics = new Electronics("002","SmartPhone",2000d,"New", 6);
        System.out.println("Khởi tạo thành công đối tượng");

        LocalDateTime time = LocalDateTime.parse("2026-05-22T00:00:00");
        System.out.println("Time1: " + time);
        LocalDateTime time2 = LocalDateTime.parse("2026-05-21T00:00:00");

        manager.createSession("001",car,a,2000d,200d,time);
        manager.createSession("002",car,a,2000d,100,time,time2);

        AuctionSession sessions = manager.getSession("001");
        manager.placeBid(sessions,b1,2100d);
        manager.placeBid(sessions,b2,2300d);
        manager.placeBid(sessions,b3,2500d);
        manager.placeBid(sessions,a,2500d);
        manager.placeBid(sessions,b3,2700d);

        AuctionSession session2 = manager.getSession("002");
        manager.placeBid(session2,b1,2100d);
        manager.placeBid(session2,b2,3000d);


    }
}
