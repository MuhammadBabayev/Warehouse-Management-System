package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.zone.CreateZoneDto;
import az.microservice.werehouseapplication.model.dto.request.zone.UpdateZoneDto;
import az.microservice.werehouseapplication.model.dto.response.zone.ZoneResponseDto;
import az.microservice.werehouseapplication.model.entity.warehouse.Zone;

import java.util.List;

public interface IZoneService {
    ZoneResponseDto createZone(CreateZoneDto dto);
    ZoneResponseDto getZoneById(Long id);
    List<ZoneResponseDto> getAllZone();
    List<ZoneResponseDto> getAllByWarehouseId(Long warehouseId);
    ZoneResponseDto updateZone(Long id, UpdateZoneDto dto);
    void deleteZone(Long id);
    Zone getZoneEntityById(Long id);

}
