package emailservice.core.exception;

public class RecipientRequiredException extends RuntimeException {
    public RecipientRequiredException(String message) {
        super(message);
    }
}
