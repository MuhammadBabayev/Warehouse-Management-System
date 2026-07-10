package az.microservice.werehouseapplication.model.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateCategoryDto {

    @NotBlank(message = "Category name cannot be empty")
    private String name;
    private String description;
    private List<CreateChildCategoryDto > children;
}
