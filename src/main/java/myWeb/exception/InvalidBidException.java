package myWeb.exception;

public class InvalidBidException extends AuctionPlatformException {
    public InvalidBidException(String message) {
        super(message);
    }
}
