package Controller;
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
class AuthenticationException extends RuntimeException{
    public AuthenticationException() {
        super("Phiên đấu giá không hợp lệ");
    }
    public AuthenticationException(String message){
        super(message);
    }
}