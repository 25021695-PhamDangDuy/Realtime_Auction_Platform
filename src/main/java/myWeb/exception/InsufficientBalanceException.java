package myWeb.exception;

public class InsufficientBalanceException extends AuctionPlatformException{
    public InsufficientBalanceException(String message){
        super(message);
    }

}
