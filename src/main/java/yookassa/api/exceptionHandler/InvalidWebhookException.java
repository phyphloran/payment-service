package yookassa.api.exceptionHandler;

public class InvalidWebhookException extends RuntimeException {
    public InvalidWebhookException(String message) {
        super(message);
    }

    public InvalidWebhookException() {
    }
}
