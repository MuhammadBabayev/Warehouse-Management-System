package az.microservice.werehouseapplication.model.dto.request.authentication;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    private String username;
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private String password;
}
