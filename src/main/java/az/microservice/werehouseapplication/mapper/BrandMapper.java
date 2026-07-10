package az.microservice.werehouseapplication.mapper;

import az.microservice.werehouseapplication.model.dto.response.brand.BrandResponseDto;
import az.microservice.werehouseapplication.model.entity.product.Brand;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BrandMapper {
    BrandResponseDto toResponseDto(Brand brand);

    List<BrandResponseDto> toResponseDtoList(List<Brand> brands);

}