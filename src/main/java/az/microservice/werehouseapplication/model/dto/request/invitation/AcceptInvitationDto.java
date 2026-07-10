package az.microservice.werehouseapplication.model.dto.request.invitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcceptInvitationDto {
    @NotBlank(message = "Token cannot be empty.")
    private String token;

    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;

    @NotBlank(message = "Password repetition cannot be empty.")
    private String confirmPassword;
}
