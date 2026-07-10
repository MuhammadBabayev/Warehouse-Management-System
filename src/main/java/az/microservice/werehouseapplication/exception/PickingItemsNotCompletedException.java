package az.microservice.werehouseapplication.exception;

public class PickingItemsNotCompletedException extends RuntimeException {
    public PickingItemsNotCompletedException(String message) {
        super(message);
    }
}
