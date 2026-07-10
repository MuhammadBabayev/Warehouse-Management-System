package az.microservice.werehouseapplication.exception;

public class InvalidInvoiceStatusException extends RuntimeException {
    public InvalidInvoiceStatusException(String message) {
        super(message);
    }
}
