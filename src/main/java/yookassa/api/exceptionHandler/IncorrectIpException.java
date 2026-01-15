package yookassa.api.exceptionHandler;

public class IncorrectIpException extends RuntimeException {
    public IncorrectIpException(String message) {
        super(message);
    }
}
