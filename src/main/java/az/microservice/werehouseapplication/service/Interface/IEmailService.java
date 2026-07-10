package az.microservice.werehouseapplication.service.Interface;

public interface IEmailService {
    void sendInvitationEmail(String to, String token);
}
