package myWeb.controller;

public enum AuctionStatus {
    // enum: liệt kê các trạng thái
    // định nghĩa các trạng thái phiên đấu giá
    OPEN,
    RUNNING,
    FINISHED,
    PAID,
    CANCELED,
}
class Auction{
    private AuctionStatus status = AuctionStatus.OPEN;
    //Đảm bảo an toàn đa luồng
    public synchronized void transitionTo(AuctionStatus nextStatus){
        if (isValidTransition(this.status, nextStatus)){
            System.out.println("Chuyển trạng thái:" + this.status + "->" + nextStatus);
            this.status = nextStatus;
        } else {
            throw new IllegalStateException("Không thể chuyển từ " + this.status + "->" + nextStatus);
        }
    }
    private boolean isValidTransition(AuctionStatus current, AuctionStatus next){
        if (current == AuctionStatus.OPEN){
            return next == AuctionStatus.RUNNING || next == AuctionStatus.CANCELED; //hủy nếu cuộc đấu giá vi phạm.
        } else if (current == AuctionStatus.RUNNING){
            return next == AuctionStatus.FINISHED || next == AuctionStatus.CANCELED; //hủy nếu ai đó gian lận trong cuộc đấu giá.
        } else if (current == AuctionStatus.FINISHED){
            return next == AuctionStatus.PAID;
        }
        return false;
    }

}

