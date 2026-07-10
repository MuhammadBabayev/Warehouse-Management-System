package az.microservice.werehouseapplication.model.dto.request.partner;

import az.microservice.werehouseapplication.enums.PartnerStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePartnerDto {

    @NotBlank(message = "Vendor name cannot be empty")
    private String name;

    private Long organizationId;

    private String contactPerson;

    private String phone;

    private String email;

    private String address;

    private PartnerStatus status;
}
