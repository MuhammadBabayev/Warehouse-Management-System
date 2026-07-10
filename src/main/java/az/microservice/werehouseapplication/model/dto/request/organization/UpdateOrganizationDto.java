package az.microservice.werehouseapplication.model.dto.request.organization;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrganizationDto {
    private String name;

    private String address;

    private String phone;

    @Email(message = "Email düzgün deyil")
    private String email;
}
