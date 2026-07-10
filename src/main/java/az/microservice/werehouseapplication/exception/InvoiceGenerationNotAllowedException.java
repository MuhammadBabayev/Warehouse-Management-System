package az.microservice.werehouseapplication.exception;

public class InvoiceGenerationNotAllowedException extends RuntimeException {
    public InvoiceGenerationNotAllowedException(String message) {
        super(message);
    }
}
