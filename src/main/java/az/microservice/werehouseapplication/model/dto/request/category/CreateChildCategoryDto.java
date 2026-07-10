package az.microservice.werehouseapplication.model.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChildCategoryDto {

    @NotBlank(message = "Child category name cannot be empty")
    private String name;

    private String description;
}
