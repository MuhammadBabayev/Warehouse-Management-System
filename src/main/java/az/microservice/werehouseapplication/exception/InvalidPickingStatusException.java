package az.microservice.werehouseapplication.exception;

public class InvalidPickingStatusException extends RuntimeException {
    public InvalidPickingStatusException(String message) {
        super(message);
    }
}
