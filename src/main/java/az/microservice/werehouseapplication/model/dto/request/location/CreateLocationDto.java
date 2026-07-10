package az.microservice.werehouseapplication.model.dto.request.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLocationDto {

    @NotNull(message = "Shelf id cannot be empty")
    private Long shelfId;

    @NotBlank(message = "Location code cannot be empty")
    private String code;

    private Long maxCapacity;

    private String description;
}
