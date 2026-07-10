package az.microservice.werehouseapplication.model.dto.response.category;

import az.microservice.werehouseapplication.model.entity.product.ChildCategory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryResponseDto {
    private String name;
    private String description;
    private List<ChildCategory> childCategories;
}
