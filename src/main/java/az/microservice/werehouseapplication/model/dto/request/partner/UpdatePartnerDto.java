package az.microservice.werehouseapplication.model.dto.request.partner;

import az.microservice.werehouseapplication.enums.PartnerStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePartnerDto {

    private String name;

    private String contactPerson;

    private String phone;

    private String email;

    private String address;

    private PartnerStatus status;
}
