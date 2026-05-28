package myWeb.exception;

public class AuctionClosedException extends AuctionPlatformException {
    public AuctionClosedException(String message) {
        super(message);
    }
}
