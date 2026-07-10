package az.microservice.werehouseapplication.mapper;

import az.microservice.werehouseapplication.model.dto.response.category.ChildCategoryResponseDto;
import az.microservice.werehouseapplication.model.entity.product.ChildCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ChildCategoryMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ChildCategoryResponseDto toResponseDto(ChildCategory childCategory);
}
