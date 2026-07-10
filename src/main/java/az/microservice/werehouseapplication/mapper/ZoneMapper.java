package az.microservice.werehouseapplication.mapper;

import az.microservice.werehouseapplication.model.dto.request.zone.CreateZoneDto;
import az.microservice.werehouseapplication.model.dto.request.zone.UpdateZoneDto;
import az.microservice.werehouseapplication.model.dto.response.zone.ZoneResponseDto;
import az.microservice.werehouseapplication.model.entity.warehouse.Zone;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ZoneMapper {
    @Mapping(target = "warehouse", ignore = true)
    Zone toEntity(CreateZoneDto dto);

    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    ZoneResponseDto toResponseDto(Zone entity);

    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    List<ZoneResponseDto> toResponseDtoList(List<Zone> zones);

    @Mapping(target = "warehouse", ignore = true)
    void updateEntityFromDto(UpdateZoneDto dto, @MappingTarget Zone zone);
}
