package az.microservice.werehouseapplication.model.dto.request.permission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePermissionDto {

    private String name;

    private String description;
}
