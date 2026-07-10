package az.microservice.werehouseapplication.model.dto.response.category;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildCategoryResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
}
