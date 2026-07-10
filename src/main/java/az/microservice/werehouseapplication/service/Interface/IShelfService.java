package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.shelf.CreateShelfDto;
import az.microservice.werehouseapplication.model.dto.request.shelf.UpdateShelfDto;
import az.microservice.werehouseapplication.model.dto.response.shelf.ShelfResponseDto;
import az.microservice.werehouseapplication.model.entity.warehouse.Shelf;

import java.util.List;

public interface IShelfService {
    ShelfResponseDto createShelf(CreateShelfDto request);
    ShelfResponseDto getShelfById(Long id);
    List<ShelfResponseDto> getAllShelf();
    List<ShelfResponseDto> getAllByZoneId(Long zoneId);
    ShelfResponseDto updateShelf(Long id, UpdateShelfDto request);
    void deleteShelf(Long id);
    Shelf getShelfEntityById(Long id);

}
