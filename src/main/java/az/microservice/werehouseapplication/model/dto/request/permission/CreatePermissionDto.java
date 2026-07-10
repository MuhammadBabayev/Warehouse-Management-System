package az.microservice.werehouseapplication.model.dto.request.permission;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePermissionDto {

    @NotBlank(message = "Permission name cannot be empty")
    private String name;

    private String description;
}
