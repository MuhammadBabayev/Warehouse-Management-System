package az.microservice.werehouseapplication.mapper;

import az.microservice.werehouseapplication.model.dto.response.category.CategoryResponseDto;
import az.microservice.werehouseapplication.model.entity.product.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CategoryMapper {
    CategoryResponseDto toResponseDto(Category category);
    List<CategoryResponseDto> toResponseDtoList(List<Category> category);

}
