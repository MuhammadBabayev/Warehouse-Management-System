package az.microservice.werehouseapplication.model.dto.request.warehouse;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateWarehouseDto {

    @NotBlank(message = "Name is required")
    private String name;
    private String address;
    private String phone;
}
