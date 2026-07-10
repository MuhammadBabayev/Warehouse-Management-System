package az.microservice.werehouseapplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface StockMovementMapper {
//    @Mapping(source = "product.id", target = "productId")
//    @Mapping(source = "product.name", target = "productName")
//    @Mapping(source = "location.id", target = "locationId")
//    @Mapping(source = "location.code", target = "locationName")
//    @Mapping(source = "performedBy.id", target = "performedById")
//    @Mapping(source = "performedBy.username", target = "performedByUsername")
//    StockMovementResponseDto toResponseDto(StockMovement stockMovement);

}
