package az.microservice.werehouseapplication.exception;

public class InvalidCheckStatusException extends RuntimeException {
    public InvalidCheckStatusException(String message) {
        super(message);
    }
}
