package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.enums.MovementType;
import az.microservice.werehouseapplication.model.dto.response.transfer.StockMovementResponseDto;
import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;

import java.time.LocalDateTime;
import java.util.List;

public interface IStockMovementService {
    List<StockMovementResponseDto> getMovementsByProduct(Long productId);
    List<StockMovementResponseDto> getMovementsByWarehouse(Long warehouseId);
//    List<StockMovementResponseDto> getMovementsByReference(String referenceType, Long referenceId);
    List<StockMovementResponseDto> getMovementsByDateRange(LocalDateTime from, LocalDateTime to);

    List<StockMovementResponseDto> getAll();
//    List<StockMovementResponseDto> getAllByProductId(Long productId);
    List<StockMovementResponseDto> getAllByLocationId(Long locationId);

    // Digər service-lər tərəfindən çağırılır
    void record(Product product,
                Location location,
                User performedBy,
                MovementType type,
                Integer quantity,
                Integer quantityBefore,
                Integer quantityAfter,
                String notes);
}
