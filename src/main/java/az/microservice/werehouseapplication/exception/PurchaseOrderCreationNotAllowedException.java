package az.microservice.werehouseapplication.exception;

public class PurchaseOrderCreationNotAllowedException extends RuntimeException {
    public PurchaseOrderCreationNotAllowedException(String message) {
        super(message);
    }
}
