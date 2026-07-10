package az.microservice.werehouseapplication.exception;

public class ShipmentCreationNotAllowedException extends RuntimeException {
    public ShipmentCreationNotAllowedException(String message) {
        super(message);
    }
}
