package exception;

public class UnthorizedActionException extends RuntimeException {
    public UnthorizedActionException(String message) {
        super(message);
    }
}
