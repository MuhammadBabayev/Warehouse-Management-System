package az.microservice.werehouseapplication.exception;

public class DuplicateInvoiceCheckException extends RuntimeException {
    public DuplicateInvoiceCheckException(String message) {
        super(message);
    }
}
