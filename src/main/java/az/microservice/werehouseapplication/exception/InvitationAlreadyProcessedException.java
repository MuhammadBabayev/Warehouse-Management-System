package az.microservice.werehouseapplication.exception;

public class InvitationAlreadyProcessedException extends RuntimeException {
    public InvitationAlreadyProcessedException(String message) {
        super(message);
    }
}
