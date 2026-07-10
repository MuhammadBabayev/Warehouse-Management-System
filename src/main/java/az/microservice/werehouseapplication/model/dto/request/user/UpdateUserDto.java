package az.microservice.werehouseapplication.model.dto.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDto {
    private String lastName;
    private String phone;
    private Long organizationId;
}
