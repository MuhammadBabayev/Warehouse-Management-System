package az.microservice.werehouseapplication.model.dto.request.brand;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBrandDto {
    @NotBlank(message = "Brand name cannot be empty")
    private String name;

    private String description;

    private Long organizationId;
}
