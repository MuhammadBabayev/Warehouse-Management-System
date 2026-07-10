package az.microservice.werehouseapplication.exception;

public class ShipmentNotReadyForShippingException extends RuntimeException {
    public ShipmentNotReadyForShippingException(String message) {
        super(message);
    }
}
