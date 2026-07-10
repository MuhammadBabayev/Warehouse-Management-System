package az.microservice.werehouseapplication.mapper;

import az.microservice.werehouseapplication.model.dto.request.shelf.CreateShelfDto;
import az.microservice.werehouseapplication.model.dto.request.shelf.UpdateShelfDto;
import az.microservice.werehouseapplication.model.dto.response.shelf.ShelfResponseDto;
import az.microservice.werehouseapplication.model.entity.warehouse.Shelf;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ShelfMapper {
    @Mapping(target = "zone", ignore = true)
    Shelf toEntity(CreateShelfDto dto);

    @Mapping(source = "zone.id", target = "zoneId")
    @Mapping(source = "zone.name", target = "zoneName")
    ShelfResponseDto toResponseDto(Shelf shelf);

    @Mapping(source = "zone.id", target = "zoneId")
    @Mapping(source = "zone.name", target = "zoneName")
    List<ShelfResponseDto> toResponseDtoList(List<Shelf> shelves);

    @Mapping(target = "zone", ignore = true)
    void updateEntityFromDto(UpdateShelfDto dto, @MappingTarget Shelf shelf);

}
