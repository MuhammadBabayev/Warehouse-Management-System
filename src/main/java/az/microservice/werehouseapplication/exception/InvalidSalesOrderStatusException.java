package az.microservice.werehouseapplication.exception;

public class InvalidSalesOrderStatusException extends RuntimeException {
    public InvalidSalesOrderStatusException(String message) {
        super(message);
    }
}
