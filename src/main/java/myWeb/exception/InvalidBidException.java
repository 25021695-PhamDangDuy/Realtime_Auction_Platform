package myWeb.exception;

class InvalidBidException extends RuntimeException {
    public InvalidBidException(){
        super("Giá thầu không hợp lệ");
    }
    public InvalidBidException(String message){
        super(message);
    }
}
class AuctionClosedException extends RuntimeException{
    public AuctionClosedException(){
        super("Phiên đấu giá đã đóng");
    }
    public AuctionClosedException(String message){
        super(message);
    }

}
class AuthentificationException extends RuntimeException{
    public AuthentificationException() {
        super("Phiên đấu giá không hợp lệ");
    }
    public AuthentificationException(String message){
        super(message);
    }
}

