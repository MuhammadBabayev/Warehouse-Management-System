package az.microservice.werehouseapplication.exception.old;

public class InvalidTransferStateException extends RuntimeException {
    public InvalidTransferStateException(String message) {
        super(message);
    }
}
