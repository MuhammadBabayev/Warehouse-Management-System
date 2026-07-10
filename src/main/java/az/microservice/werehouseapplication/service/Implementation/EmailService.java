package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.service.Interface.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {
    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void sendInvitationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Invitation to the Warehouse system");
        message.setText(
                "Hello!\n\n" +
                        "You have been invited to the Warehouse system.\n" +
                        "Go to the link below and set your password.:\n\n" +
                        frontendUrl + "/invite?token=" + token + "\n\n" +
                        "The link is valid for 24 hours..\n\n" +
                        "Sincerely,\nWarehouse Team"
        );
        mailSender.send(message);
    }
}
