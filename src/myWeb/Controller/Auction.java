package myWeb.Controller;

enum AuctionStatus {
    //định nghĩa trạng thái phiên đấu giá
    OPEN,
    RUNNING,
    FINISH,//kết thúc thời gian đấu giá
    PAID,//đã thanh toán
    CANCELED,//bị hủy
}
class Auction{
    private AuctionStatus status = AuctionStatus.OPEN;
    //Đảm bảo an toàn đa luồng
    public synchronized void transitionTo(AuctionStatus nextStatus){
        if (isValidTransition(this.status,nextStatus)) {
            System.out.println("Chuyển trạng thái:" + this.status + "->" + nextStatus);
            this.status = nextStatus;
        } else {
            throw new IllegalStateException("Không thể chuyển trạng thái từ" + this.status + "sang" + nextStatus);
        }
    }
    private boolean isValidTransition(AuctionStatus current,AuctionStatus next){
        if (current == AuctionStatus.OPEN){
            return next == AuctionStatus.RUNNING || next == AuctionStatus.CANCELED;//cancel nếu phiên đấu giá không hợp lệ.
        } else if (current == AuctionStatus.RUNNING){
            return next == AuctionStatus.FINISH || next == AuctionStatus.CANCELED;//cancel nếu có một người gian lận.
        } else if (current == AuctionStatus.FINISH){
            return next == AuctionStatus.PAID || next == AuctionStatus.CANCELED;
        } else {
            return false;
        }

    }
}

