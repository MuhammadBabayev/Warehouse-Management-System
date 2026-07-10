package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.response.inventory.InventoryQuantityResponseDto;
import az.microservice.werehouseapplication.model.dto.response.transfer.InventoryResponseDto;
import az.microservice.werehouseapplication.model.dto.response.transfer.LowStockAlertDto;
import az.microservice.werehouseapplication.model.dto.response.transfer.ProductStockSummaryDto;
import az.microservice.werehouseapplication.model.entity.transfer.Inventory;

import java.util.List;

public interface IInventoryService {

    InventoryResponseDto create(Long productId, Long locationId, Integer quantity);
    List<InventoryResponseDto> getAll();
    List<InventoryResponseDto> getAllByProductId(Long productId);
    List<InventoryResponseDto> getAllByLocationId(Long locationId);
    List<InventoryResponseDto> getAllByWarehouseId(Long warehouseId);
    List<LowStockAlertDto> getLowStockAlerts();
    ProductStockSummaryDto getProductStockSummary(Long productId);
    int getStockQuantity(Long productId, Long locationId);
    int getDamagedStockQuantity(Long productId, Long locationId);
    void reserveStock(Long productId, Long locationId, Integer quantity);
    Inventory getInventoryEntity(Long productId, Long locationId);
    void releaseReservedStock(Long productId, Long locationId, Integer quantity);
    void markAsDamaged(Long productId, Long locationId, Integer quantity);
    void writeDamagedOff(Long productId, Long locationId, Integer quantity);
    InventoryQuantityResponseDto getQuantitiesById(Long inventoryId);
    // Digər service-lər tərəfindən çağırılır
    void increaseStock(Long productId, Long locationId, Integer quantity);

    void decreaseStock(Long productId, Long locationId, Integer quantity);

//    void increaseDamagedStock(Long productId, Long locationId, Integer quantity,
//                              ReferenceType referenceType, Long referenceId);


}
