package az.microservice.werehouseapplication.model.dto.request.authentication;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    @NotBlank(message = "Username can't be empty")
    private String username;

    @NotBlank(message = "password can't be null")
    private String password;
}
