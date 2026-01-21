package yookassa.api.exceptionHandler;

public class IdempotenceKeyConflictException extends RuntimeException {
    public IdempotenceKeyConflictException(String message) {
        super(message);
    }
}
