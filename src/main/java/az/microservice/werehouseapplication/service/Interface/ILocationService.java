package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.location.CreateLocationDto;
import az.microservice.werehouseapplication.model.dto.request.location.UpdateLocationDto;
import az.microservice.werehouseapplication.model.dto.request.location.UpdateLocationStatusDto;
import az.microservice.werehouseapplication.model.dto.response.location.LocationResponseDto;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;

import java.util.List;

public interface ILocationService {
    LocationResponseDto create(CreateLocationDto dto);
    LocationResponseDto getById(Long id);
    List<LocationResponseDto> getAll();
    List<LocationResponseDto> getAllByShelfId(Long shelfId);
    LocationResponseDto update(Long id, UpdateLocationDto dto);
    void updateStatus(Long id, UpdateLocationStatusDto dto);
    void delete(Long id);
    Location getLocationEntityById(Long id);
}
