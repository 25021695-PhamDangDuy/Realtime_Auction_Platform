package myWeb.controller;

public class AuctionSession {
    private String produceId;
    private Item item;
    private Seller seller;

    private double currentPrice;//giá hiện tại
    private double minIncreament; // bước giá tối thiểu
    private Bidder topBidder; //người trả giá cao nhất hiện tại

    private LocalDateTime endtime;// thời gian kết thúc
    private boolean isActive=true;// trạng thái phiên

    public AuctionSession(String produceId,Seller seller,double startPrice,double minIncreament,LocalDateTime endtime) {
        this.produceId = produceId;
        this.seller=seller;
        this.currentPrice=startPrice;
        this.minIncreament=minIncreament;
        this.endtime=endtime;

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
