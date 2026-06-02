package exception;

public class NotificationDelivaryException extends RuntimeException {
    public NotificationDelivaryException(String message) {
        super(message);
    }
}
