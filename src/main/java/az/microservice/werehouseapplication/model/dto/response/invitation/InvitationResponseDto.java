package az.microservice.werehouseapplication.model.dto.response.invitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationResponseDto {
    @NotBlank(message = "Token boş ola bilməz")
    private String token;

    @NotBlank(message = "Ad boş ola bilməz")
    private String fullName;

    @NotBlank(message = "Şifrə boş ola bilməz")
    @Size(min = 6, message = "Şifrə minimum 6 simvol olmalıdır")
    private String password;

    private String phone;
}
