package az.microservice.werehouseapplication.exception;

public class ShipmentReturnNotAllowedException extends RuntimeException {
    public ShipmentReturnNotAllowedException(String message) {
        super(message);
    }
}
