package myWeb.controller;

public class AuctionSession {
    String produceId;
    double currentPrice;//giá hiện tại
    double reservePrice;//giá khởi điểm
    String topBidder;//người thắng cuộc.
    AuctionStatus state;//trạng thái

    public AuctionSession(String produceId,double reservePrice) {
        this.produceId = produceId;
        this.reservePrice = reservePrice;
        this.currentPrice = reservePrice;
        this.state = AuctionStatus.OPEN;

    }

    public String getProduceId() {
        return produceId;
    }

    public synchronized boolean placeBid(String bidderName, double bidAmount){
        if (this.state != AuctionStatus.RUNNING){
            System.out.println("Phiên đấu giá chưa bắt đầu hoặc đã kết thúc");
            return false;
        }
        if (bidAmount <= this.currentPrice){
            System.out.println("Giá đặt phải cao hơn giá hiện tại:" + currentPrice);
            return false;

        }
        this.currentPrice = bidAmount;
        this.topBidder = bidderName;
    return true;
    }

}
