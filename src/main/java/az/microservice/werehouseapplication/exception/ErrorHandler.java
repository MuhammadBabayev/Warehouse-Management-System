package az.microservice.werehouseapplication.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handle(NotFoundException exception){
        log.error("NotFoundException", exception );
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(AlreadyExistException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(AlreadyExistException exception){
        log.error("AlreadyExistException", exception );
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse handle(InvalidPasswordException exception){
        log.error("InvalidPasswordException", exception );
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(PasswordMatchException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse handle(PasswordMatchException exception){
        log.error("PasswordMatchException", exception );
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(InvalidTransferStatusException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(InvalidTransferStatusException exception){
        log.error("InvalidTransferStatusException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(InternalServerError.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(InternalServerError exception){
        log.error("InternalServerError: ", exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(PasswordMismatchException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(PasswordMismatchException exception){
        log.error("PasswordMismatchException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(InvitationAlreadyProcessedException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(InvitationAlreadyProcessedException exception){
        log.error("InvitationAlreadyProcessedException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler(InvitationExpiredException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(InvitationExpiredException exception){
        log.error("InvitationExpiredException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler(DuplicateInvoiceCheckException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(DuplicateInvoiceCheckException exception){
        log.error("DuplicateInvoiceCheckException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler(InvalidInvoiceStatusException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(InvalidInvoiceStatusException exception){
        log.error("InvalidInvoiceStatusException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler(InvalidOrderStatusException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(InvalidOrderStatusException exception){
        log.error("InvalidOrderStatusException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler(InvalidPickingStatusException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(InvalidPickingStatusException exception){
        log.error("InvalidPickingStatusException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler(InvalidSalesOrderStatusException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(InvalidSalesOrderStatusException exception){
        log.error("InvalidSalesOrderStatusException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler(PickingItemsNotCompletedException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(PickingItemsNotCompletedException exception){
        log.error("PickingItemsNotCompletedException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler(PurchaseOrderCreationNotAllowedException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorResponse handle(PurchaseOrderCreationNotAllowedException exception){
        log.error("PurchaseOrderCreationNotAllowedException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler(UnauthorizedActionException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorResponse handle(UnauthorizedActionException exception){
        log.error("UnauthorizedActionException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler(ShipmentCreationNotAllowedException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(ShipmentCreationNotAllowedException exception){
        log.error("ShipmentCreationNotAllowedException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler(ShipmentNotReadyForShippingException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(ShipmentNotReadyForShippingException exception){
        log.error("ShipmentNotReadyForShippingException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler(ShipmentReturnNotAllowedException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(ShipmentReturnNotAllowedException exception){
        log.error("ShipmentReturnNotAllowedException: ", exception);
        return new ErrorResponse(exception.getMessage());
    }

}
