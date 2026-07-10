package az.microservice.werehouseapplication.mapper;

import az.microservice.werehouseapplication.model.dto.request.product.UpdateProductDto;
import az.microservice.werehouseapplication.model.entity.product.Product;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductMapper {
//    @Mapping(source = "category.name", target = "categoryName")
//    @Mapping(source = "brand.name", target = "brandName")
//    @Mapping(source = "primaryBarcode", target = "barcode")
//    ProductResponseDto toResponseDto(Product product);

//    List<ProductResponseDto> toResponseDtoList(List<Product> products);
//
//    @Mapping(target = "id", ignore = true)
//    Product toEntity(CreateProductDto dto);

    void updateProductFromDto(UpdateProductDto dto, @MappingTarget Product product);


}
