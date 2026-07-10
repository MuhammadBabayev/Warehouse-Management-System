package az.microservice.werehouseapplication.model.dto.response.partner;

import az.microservice.werehouseapplication.enums.PartnerStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerResponseDto {

    private Long id;

    private String name;

    private String contactPerson;

    private String phone;

    private String email;

    private String address;

    private PartnerStatus status;

    private String organizationName;
}
