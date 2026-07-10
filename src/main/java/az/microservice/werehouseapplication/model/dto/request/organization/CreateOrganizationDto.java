package az.microservice.werehouseapplication.model.dto.request.organization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrganizationDto {

    @NotBlank(message = "Ad boş ola bilməz")
    private String name;

    @Email(message = "Email düzgün deyil")
    private String email;

    private String address;
    private String phone;
    private Long userId;
}