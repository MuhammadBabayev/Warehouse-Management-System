package az.microservice.werehouseapplication.exception;

public class InvalidTransferStatusException extends RuntimeException {
    public InvalidTransferStatusException(String message) {
        super(message);
    }
}
