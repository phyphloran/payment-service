package yookassa.api.exceptionHandler;

public class PaymentAlreadyExists extends RuntimeException {
    public PaymentAlreadyExists(String message) {
        super(message);
    }
}
