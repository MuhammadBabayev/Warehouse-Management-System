package az.microservice.werehouseapplication.mapper;

import az.microservice.werehouseapplication.model.dto.request.barcode.CreateBarcodeDto;
import az.microservice.werehouseapplication.model.dto.response.barcode.BarcodeResponseDto;
import az.microservice.werehouseapplication.model.entity.product.WarehouseProductBarcode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BarcodeMapper {

    @Mapping(target = "id", ignore = true)
    WarehouseProductBarcode toEntity(CreateBarcodeDto dto);

    BarcodeResponseDto toResponseDto(WarehouseProductBarcode barcode);
}
