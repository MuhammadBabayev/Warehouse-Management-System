package az.microservice.werehouseapplication.model.dto.request.shelf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShelfDto {
    @NotNull(message = "Zone ID is required")
    private Long zoneId;

    @NotBlank(message = "Code is required")
    private String code;
    private String description;
}
