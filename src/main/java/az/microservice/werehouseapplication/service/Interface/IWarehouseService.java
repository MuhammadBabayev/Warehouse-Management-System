package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.warehouse.ChangeWarehouseOrganizationDto;
import az.microservice.werehouseapplication.model.dto.request.warehouse.CreateWarehouseDto;
import az.microservice.werehouseapplication.model.dto.request.warehouse.UpdateWarehouseDto;
import az.microservice.werehouseapplication.model.dto.response.warehouse.WarehouseResponseDto;
import az.microservice.werehouseapplication.model.entity.warehouse.Warehouse;

import java.util.List;

public interface IWarehouseService {
    WarehouseResponseDto createWarehouse(CreateWarehouseDto dto);
    WarehouseResponseDto getWarehouseById(Long id);
    List<WarehouseResponseDto> getWarehouseByOrganizationId(Long organizationId);
    List<WarehouseResponseDto> getAllWarehouse();
    WarehouseResponseDto updateWarehouse(Long id, UpdateWarehouseDto updatedWarehouse);
    WarehouseResponseDto changeOrganization(Long warehouseId, ChangeWarehouseOrganizationDto dto);
    void deactiveWarehouse(Long id);
    Warehouse getWarehouseEntityById(Long id);
}
