package emailservice.core.exception;

public class EmailSenderException extends RuntimeException {
    public EmailSenderException(String message, Throwable cause) {
        super(message, cause);
    }
}
