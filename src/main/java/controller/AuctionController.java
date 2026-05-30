package controller;
import function.SessionStatus;
import models.AuctionSession;

public class AuctionController {
    private AuctionSession session;
    public void endSession(){
        session.setStatus(SessionStatus.FINISHED);
        session.notifyObservers("Phiên đấu giá đã kết thúc!Đã tìm ra người thắng cuộc");
    }
}
