package myWeb.exception;

public class BidConflictException extends RuntimeException {
    public BidConflictException(String message) {
        super(message);
    }
}
