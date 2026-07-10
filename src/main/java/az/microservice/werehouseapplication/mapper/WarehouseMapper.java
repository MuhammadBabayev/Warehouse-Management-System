package az.microservice.werehouseapplication.mapper;

import az.microservice.werehouseapplication.model.dto.request.warehouse.UpdateWarehouseDto;
import az.microservice.werehouseapplication.model.dto.response.warehouse.WarehouseResponseDto;
import az.microservice.werehouseapplication.model.entity.warehouse.Warehouse;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WarehouseMapper {

    @Mapping(source = "organization.id", target = "organizationId")
    @Mapping(source = "organization.name", target = "organizationName")
    WarehouseResponseDto toDto(Warehouse warehouse);

    @Mapping(source = "organization.id", target = "organizationId")
    @Mapping(source = "organization.name", target = "organizationName")
    List<WarehouseResponseDto> toDtoList(List<Warehouse> warehouses);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "organization", ignore = true)
//    Warehouse toEntity(CreateWarehouseDto dto);


    void updateFromDto(UpdateWarehouseDto dto, @MappingTarget Warehouse warehouse);
}
