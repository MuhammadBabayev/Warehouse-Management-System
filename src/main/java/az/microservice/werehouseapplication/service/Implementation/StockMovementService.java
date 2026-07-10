package az.microservice.werehouseapplication.service.Implementation;


import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.MovementType;
import az.microservice.werehouseapplication.mapper.StockMovementMapper;
import az.microservice.werehouseapplication.model.dto.response.transfer.StockMovementResponseDto;
import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.transfer.StockMovement;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;
import az.microservice.werehouseapplication.repository.ProductRepository;
import az.microservice.werehouseapplication.repository.StockMovementRepository;
import az.microservice.werehouseapplication.repository.WarehouseRepository;
import az.microservice.werehouseapplication.service.Interface.IStockMovementService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class StockMovementService implements IStockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockMovementMapper stockMovementMapper;

    @Override
    public List<StockMovementResponseDto> getMovementsByProduct(Long productId) {
        productRepository.findByIdAndStatus(productId, ItemStatus.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        return stockMovementRepository.findByProductId(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<StockMovementResponseDto> getMovementsByWarehouse(Long warehouseId) {
        warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found: " + warehouseId));

        return stockMovementRepository.findByWarehouseId(warehouseId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

//    @Override
//    public List<StockMovementResponseDto> getMovementsByReference(String referenceType, Long referenceId) {
//        return stockMovementRepository
//                .findByReferenceTypeAndReferenceId(referenceType, referenceId)
//                .stream()
//                .map(this::toResponse)
//                .toList();
//    }

    @Override
    public List<StockMovementResponseDto> getMovementsByDateRange(LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' date must be before 'to' date");
        }
        return stockMovementRepository.findByCreatedAtBetween(from, to)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<StockMovementResponseDto> getAll() {
        log.info("Fetching all stock movements");

        List<StockMovement> movements = stockMovementRepository.findAll();

        return movements.stream()
                .map(this::toResponse)
                .toList();
    }

//    @Override
//    public List<StockMovementResponseDto> getAllByProductId(Long productId) {
//        log.info("Fetching stock movements for product id: {}", productId);
//
//        return stockMovementRepository.findAllByProductId(productId)
//                .stream()
//                .map(this::toResponse)
//                .toList();
//    }

    @Override
    public List<StockMovementResponseDto> getAllByLocationId(Long locationId) {
        log.info("Fetching stock movements for location id: {}", locationId);

        return stockMovementRepository.findAllByLocationId(locationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void record(Product product,
                       Location location,
                       User performedBy,
                       MovementType type,
                       Integer quantity,
                       Integer quantityBefore,
                       Integer quantityAfter,
                       String notes) {

        log.info("Recording stock movement: {} for product: {} at location: {}",
                type, product.getId(), location.getId());

        StockMovement movement = StockMovement.builder()
                .product(product)
                .location(location)
                .performedBy(performedBy)
                .type(type)
                .quantity(quantity)
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .notes(notes)
                .build();

        stockMovementRepository.save(movement);
    }

    private StockMovementResponseDto toResponse(StockMovement movement) {
        return StockMovementResponseDto.builder()
                .id(movement.getId())
                .productId(movement.getProduct().getId())
                .productName(movement.getProduct().getName())
                .locationId(movement.getLocation().getId())
                .locationCode(movement.getLocation().getCode())
                .warehouseName(movement.getLocation().getShelf().getZone().getWarehouse().getName())
//                .performedByUsername(movement.getPerformedBy().getUsername())
                .type(movement.getType())
                .quantity(movement.getQuantity())
                .quantityBefore(movement.getQuantityBefore())
                .quantityAfter(movement.getQuantityAfter())
                .referenceType(movement.getReferenceType())
                .referenceId(movement.getReferenceId())
                .notes(movement.getNotes())
                .createdAt(movement.getCreatedAt())
                .build();
    }
}
