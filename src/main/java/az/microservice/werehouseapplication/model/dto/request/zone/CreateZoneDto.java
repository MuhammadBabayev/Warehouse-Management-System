package az.microservice.werehouseapplication.model.dto.request.zone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateZoneDto {
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @NotBlank(message = "Name is required")
    private String name;
    private String description;
}
