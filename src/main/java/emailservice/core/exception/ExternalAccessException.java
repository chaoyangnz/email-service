package emailservice.core.exception;

public class ExternalAccessException extends RuntimeException {
    public final static String API_SENDGRID = "sendgrid";
    public final static String API_OPENWEATHER = "openweather";

    public ExternalAccessException(String api, String message, Throwable cause) {
        super(message, cause);
    }
}
