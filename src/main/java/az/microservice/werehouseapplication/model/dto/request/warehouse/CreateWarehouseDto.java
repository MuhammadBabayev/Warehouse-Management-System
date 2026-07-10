package az.microservice.werehouseapplication.model.dto.request.warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWarehouseDto {

    @NotNull(message = "Organization ID is required")
    private Long organizationId;

    @NotBlank(message = "Name is required")
    private String name;
    private String address;
    private String phone;
}
