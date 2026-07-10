package az.microservice.werehouseapplication.mapper;

import az.microservice.werehouseapplication.model.dto.request.location.CreateLocationDto;
import az.microservice.werehouseapplication.model.dto.request.location.UpdateLocationDto;
import az.microservice.werehouseapplication.model.dto.response.location.LocationResponseDto;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LocationMapper {

    @Mapping(target = "shelf", ignore = true)
    Location toEntity(CreateLocationDto dto);

    @Mapping(source = "shelf.id", target = "shelfId")
    @Mapping(source = "shelf.code", target = "shelfCode")
    LocationResponseDto toResponseDto(Location location);

    @Mapping(source = "shelf.id", target = "shelfId")
    @Mapping(source = "shelf.code", target = "shelfCode")
    List<LocationResponseDto> toResponseDtoList(List<Location> locations);

    @Mapping(target = "shelf", ignore = true)
    void updateEntityFromDto(UpdateLocationDto dto, @MappingTarget Location entity);
}
