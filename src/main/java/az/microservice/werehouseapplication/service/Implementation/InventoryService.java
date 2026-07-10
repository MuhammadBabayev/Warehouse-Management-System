package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.MovementType;
import az.microservice.werehouseapplication.exception.AlreadyExistException;
import az.microservice.werehouseapplication.exception.InternalServerError;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.exception.old.MyException;
import az.microservice.werehouseapplication.model.dto.response.inventory.InventoryQuantityResponseDto;
import az.microservice.werehouseapplication.model.dto.response.transfer.InventoryResponseDto;
import az.microservice.werehouseapplication.model.dto.response.transfer.LowStockAlertDto;
import az.microservice.werehouseapplication.model.dto.response.transfer.ProductStockSummaryDto;
import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.transfer.Inventory;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;
import az.microservice.werehouseapplication.repository.InventoryRepository;
import az.microservice.werehouseapplication.repository.ProductRepository;
import az.microservice.werehouseapplication.repository.UserRepository;
import az.microservice.werehouseapplication.repository.WarehouseRepository;
import az.microservice.werehouseapplication.service.Interface.IInventoryService;
import az.microservice.werehouseapplication.service.Interface.ILocationService;
import az.microservice.werehouseapplication.service.Interface.IProductService;
import az.microservice.werehouseapplication.service.Interface.IStockMovementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService implements IInventoryService {

    private final InventoryRepository inventoryRepository;
    private final IProductService productService;
    private final ILocationService locationService;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final IStockMovementService stockMovementService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public InventoryResponseDto create(Long productId, Long locationId, Integer quantity) {
        log.info("Creating inventory for product id: {} at location id: {}", productId, locationId);

        inventoryRepository.findByProduct_IdAndLocation_Id(productId, locationId)
                .ifPresent(inv -> {
                    throw new AlreadyExistException(INVENTORY_ALREADY_EXIST.getMessage());
                });

        Product product = productService.getProductEntityById(productId);
        Location location = locationService.getLocationEntityById(locationId);

        // totalQuantity = available + reserved + damaged
        Inventory inventory = Inventory.builder()
                .product(product)
                .location(location)
                .availableQuantity(quantity)
                .reservedQuantity(0)
                .damagedQuantity(0)
                .totalQuantity(quantity)   // only available at creation
                .build();

        if(product.getProductCount() < inventory.getAvailableQuantity()){
           throw new InternalServerError(INVENTORY_COUNT_MORE.getMessage());
        }

        Inventory saved = inventoryRepository.save(inventory);
        log.info("Inventory record created with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public List<InventoryResponseDto> getAll() {
        log.info("Fetching all inventory");
        return inventoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    public InventoryQuantityResponseDto getQuantitiesById(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new NotFoundException(INVENTORY_NOT_FOUND.getMessage()));

        return InventoryQuantityResponseDto.builder()
                .inventoryId(inventory.getId())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .damagedQuantity(inventory.getDamagedQuantity())
                .totalQuantity(inventory.getTotalQuantity())
                .build();
    }

    @Override
    public List<InventoryResponseDto> getAllByProductId(Long productId) {
        log.info("Fetching inventory for product id: {}", productId);
        productRepository.findByIdAndStatus(productId, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND.getMessage()));

        return inventoryRepository.findAllByProduct_Id(productId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<InventoryResponseDto> getAllByLocationId(Long locationId) {
        log.info("Fetching inventory for location id: {}", locationId);
        return inventoryRepository.findAllByLocation_Id(locationId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<InventoryResponseDto> getAllByWarehouseId(Long warehouseId) {
        log.info("Fetching inventory for warehouse id: {}", warehouseId);
        warehouseRepository.findByIdAndStatus(warehouseId, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(WAREHOUSE_NOT_FOUND.getMessage()));

        return inventoryRepository.findAllByLocation_Shelf_Zone_WarehouseId(warehouseId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<LowStockAlertDto> getLowStockAlerts() {
        log.info("Fetching low stock alerts");
        return inventoryRepository.findAll().stream()
                .filter(inv -> inv.getAvailableQuantity() < inv.getProduct().getMinStock())
                .map(inv -> LowStockAlertDto.builder()
                        .productId(inv.getProduct().getId())
                        .productName(inv.getProduct().getName())
                        .locationId(inv.getLocation().getId())
                        .locationCode(inv.getLocation().getCode())
                        .warehouseName(inv.getLocation().getShelf().getZone().getWarehouse().getName())
                        .availableQuantity(inv.getAvailableQuantity())
                        .minStock(inv.getProduct().getMinStock())
                        .build())
                .toList();
    }


    @Override
    public ProductStockSummaryDto getProductStockSummary(Long productId) {
        log.info("Building stock summary for product id: {}", productId);

        Product product = productService.getProductEntityById(productId);

        List<Inventory> inventories = inventoryRepository.findAllByProduct_Id(productId);

        int totalAvailable = inventories.stream().mapToInt(Inventory::getAvailableQuantity).sum();
        int totalReserved  = inventories.stream().mapToInt(Inventory::getReservedQuantity).sum();
        int totalDamaged   = inventories.stream().mapToInt(Inventory::getDamagedQuantity).sum();
        int totalInWarehouses = totalAvailable + totalReserved + totalDamaged;

        // If Product gains a `totalStock` field later, replace 0 with product.getTotalStock()
        // and compute: unassigned = product.getTotalStock() - totalInWarehouses
        int unassigned = 0; // placeholder — see note above

        return ProductStockSummaryDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .sku(product.getSku())
                .totalAvailable(totalAvailable)
                .totalReserved(totalReserved)
                .totalDamaged(totalDamaged)
                .totalInWarehouses(totalInWarehouses)
                .unassignedStock(unassigned)
                .locationCount(inventories.size())
                .build();
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increaseStock(Long productId, Long locationId, Integer quantity) {
        log.info("Increasing stock — product: {}, location: {}, qty: {}", productId, locationId, quantity);

        if (quantity <= 0) throw new MyException("Quantity must be positive");

        User currentUser = null;

        Inventory inventory = inventoryRepository
                .findByProduct_IdAndLocation_Id(productId, locationId)
                .orElseGet(() -> createEmptyInventory(productId, locationId));

        int before = inventory.getAvailableQuantity();
        inventory.setAvailableQuantity(before + quantity);
        recalculateTotal(inventory);
        inventoryRepository.save(inventory);

        stockMovementService.record(
                inventory.getProduct(),
                inventory.getLocation(),
                currentUser,
                MovementType.INBOUND,
                quantity,
                before,
                inventory.getAvailableQuantity(),
                null
        );
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseStock(Long productId, Long locationId, Integer quantity) {
        log.info("Decreasing stock — product: {}, location: {}, qty: {}", productId, locationId, quantity);

        if (quantity <= 0) throw new MyException("Quantity must be positive");

        User currentUser = null;

        Inventory inventory = inventoryRepository
                .findByProduct_IdAndLocation_Id(productId, locationId)
                .orElseThrow(() -> new NotFoundException(INVENTORY_NOT_FOUND.getMessage()));

        if (inventory.getAvailableQuantity() < quantity)
            throw new InternalServerError(INSUFFICIENT_STOCK.getMessage());

        int before = inventory.getAvailableQuantity();
        inventory.setAvailableQuantity(before - quantity);
        recalculateTotal(inventory);
        inventoryRepository.save(inventory);

        stockMovementService.record(
                inventory.getProduct(),
                inventory.getLocation(),
                currentUser,
                MovementType.OUTBOUND,
                quantity,
                before,
                inventory.getAvailableQuantity(),
                null
        );
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reserveStock(Long productId, Long locationId, Integer quantity) {
        log.info("Reserving stock — product: {}, location: {}, qty: {}", productId, locationId, quantity);

        if (quantity <= 0) throw new MyException("Quantity must be positive");

        User currentUser = null;

        Inventory inventory = inventoryRepository
                .findByProduct_IdAndLocation_Id(productId, locationId)
                .orElseThrow(() -> new NotFoundException(INVENTORY_NOT_FOUND.getMessage()));

        if (inventory.getAvailableQuantity() < quantity)
            throw new InternalServerError(INSUFFICIENT_STOCK.getMessage());

        int before = inventory.getAvailableQuantity();
        inventory.setAvailableQuantity(before - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        // totalQuantity does NOT change — units just moved between buckets
        inventoryRepository.save(inventory);

        stockMovementService.record(
                inventory.getProduct(),
                inventory.getLocation(),
                currentUser,
                MovementType.ADJUSTMENT,
                quantity,
                before,
                inventory.getAvailableQuantity(),
                "Stock reserved"
        );
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void releaseReservedStock(Long productId, Long locationId, Integer quantity) {
        log.info("Releasing reserved stock — product: {}, location: {}, qty: {}", productId, locationId, quantity);

        if (quantity <= 0) throw new MyException("Quantity must be positive");

        User currentUser = null;

        Inventory inventory = inventoryRepository
                .findByProduct_IdAndLocation_Id(productId, locationId)
                .orElseThrow(() -> new NotFoundException(INVENTORY_NOT_FOUND.getMessage()));

        if (inventory.getReservedQuantity() < quantity)
            throw new MyException("Cannot release more than reserved quantity");

        int before = inventory.getAvailableQuantity();
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setAvailableQuantity(before + quantity);
        // totalQuantity does NOT change
        inventoryRepository.save(inventory);

        stockMovementService.record(
                inventory.getProduct(),
                inventory.getLocation(),
                currentUser,
                MovementType.ADJUSTMENT,
                quantity,
                before,
                inventory.getAvailableQuantity(),
                "Reserved stock released"
        );
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsDamaged(Long productId, Long locationId, Integer quantity) {
        log.info("Marking damaged stock — product: {}, location: {}, qty: {}", productId, locationId, quantity);

        if (quantity <= 0) throw new MyException("Quantity must be positive");

        User currentUser = null;

        Inventory inventory = inventoryRepository
                .findByProduct_IdAndLocation_Id(productId, locationId)
                .orElseThrow(() -> new NotFoundException(INVENTORY_NOT_FOUND.getMessage()));

        if (inventory.getAvailableQuantity() < quantity)
            throw new InternalServerError(INSUFFICIENT_STOCK.getMessage());

        int before = inventory.getAvailableQuantity();
        inventory.setAvailableQuantity(before - quantity);
        inventory.setDamagedQuantity(inventory.getDamagedQuantity() + quantity);
        // totalQuantity does NOT change — units moved between buckets
        inventoryRepository.save(inventory);

        stockMovementService.record(
                inventory.getProduct(),
                inventory.getLocation(),
                currentUser,
                MovementType.ADJUSTMENT,
                quantity,
                before,
                inventory.getAvailableQuantity(),
                "Marked as damaged"
        );
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void writeDamagedOff(Long productId, Long locationId, Integer quantity) {
        log.info("Writing off damaged stock — product: {}, location: {}, qty: {}", productId, locationId, quantity);

        if (quantity <= 0) throw new MyException("Quantity must be positive");

        User currentUser = null;

        Inventory inventory = inventoryRepository
                .findByProduct_IdAndLocation_Id(productId, locationId)
                .orElseThrow(() -> new NotFoundException(INVENTORY_NOT_FOUND.getMessage()));

        if (inventory.getDamagedQuantity() < quantity)
            throw new MyException("Cannot write off more than damaged quantity");

        int before = inventory.getDamagedQuantity();
        inventory.setDamagedQuantity(before - quantity);
        recalculateTotal(inventory);
        inventoryRepository.save(inventory);

        stockMovementService.record(
                inventory.getProduct(),
                inventory.getLocation(),
                currentUser,
                MovementType.OUTBOUND,
                quantity,
                before,
                inventory.getDamagedQuantity(),
                "Damaged stock written off"
        );
    }

    @Override
    public Inventory getInventoryEntity(Long productId, Long locationId) {
        return inventoryRepository.findByProduct_IdAndLocation_Id(productId, locationId)
                .orElseThrow(() -> new NotFoundException(INVENTORY_NOT_FOUND.getMessage()));
    }

    @Override
    public int getStockQuantity(Long productId, Long locationId) {
        return inventoryRepository.findByProduct_IdAndLocation_Id(productId, locationId)
                .map(Inventory::getAvailableQuantity)
                .orElse(0);
    }

    @Override
    public int getDamagedStockQuantity(Long productId, Long locationId) {
        return inventoryRepository.findByProduct_IdAndLocation_Id(productId, locationId)
                .map(Inventory::getDamagedQuantity)
                .orElse(0);
    }

    // PRIVATE HELPERS

    private void recalculateTotal(Inventory inventory) {
        inventory.setTotalQuantity(
                inventory.getAvailableQuantity()
                        + inventory.getReservedQuantity()
                        + inventory.getDamagedQuantity()
        );
    }

    private Inventory createEmptyInventory(Long productId, Long locationId) {
        Product product = productService.getProductEntityById(productId);
        Location location = locationService.getLocationEntityById(locationId);
        return inventoryRepository.save(
                Inventory.builder()
                        .product(product)
                        .location(location)
                        .availableQuantity(0)
                        .reservedQuantity(0)
                        .damagedQuantity(0)
                        .totalQuantity(0)
                        .build()
        );
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Sistemdə belə bir istifadəçi tapılmadı: " + username));
    }

    private InventoryResponseDto toResponse(Inventory inventory) {
        return InventoryResponseDto.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .locationId(inventory.getLocation().getId())
                .locationCode(inventory.getLocation().getCode())
                .shelfCode(inventory.getLocation().getShelf().getCode())
                .zoneName(inventory.getLocation().getShelf().getZone().getName())
                .warehouseName(inventory.getLocation().getShelf().getZone().getWarehouse().getName())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .damagedQuantity(inventory.getDamagedQuantity())
                .totalQuantity(inventory.getTotalQuantity())
                .build();
    }
}