package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.response.inventory.InventoryQuantityResponseDto;
import az.microservice.werehouseapplication.model.dto.response.transfer.InventoryResponseDto;
import az.microservice.werehouseapplication.model.dto.response.transfer.LowStockAlertDto;
import az.microservice.werehouseapplication.model.dto.response.transfer.ProductStockSummaryDto;
import az.microservice.werehouseapplication.service.Interface.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final IInventoryService inventoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('inventory.create')")
    public ResponseEntity<InventoryResponseDto> create(
            @RequestParam Long productId,
            @RequestParam Long locationId,
            @RequestParam Integer quantity) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(inventoryService.create(productId, locationId, quantity));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<List<InventoryResponseDto>> getAll() {
        return ResponseEntity.ok(inventoryService.getAll());
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<List<InventoryResponseDto>> getAllByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getAllByProductId(productId));
    }

    @GetMapping("/location/{locationId}")
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<List<InventoryResponseDto>> getAllByLocationId(@PathVariable Long locationId) {
        return ResponseEntity.ok(inventoryService.getAllByLocationId(locationId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<List<InventoryResponseDto>> getAllByWarehouseId(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getAllByWarehouseId(warehouseId));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<List<LowStockAlertDto>> getLowStockAlerts() {
        return ResponseEntity.ok(inventoryService.getLowStockAlerts());
    }

    @GetMapping("/product/{productId}/summary")
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<ProductStockSummaryDto> getProductStockSummary(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getProductStockSummary(productId));
    }

    @GetMapping("/stock")
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<Integer> getStockQuantity(
            @RequestParam Long productId,
            @RequestParam Long locationId) {
        return ResponseEntity.ok(inventoryService.getStockQuantity(productId, locationId));
    }

    @GetMapping("/damaged-stock")
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<Integer> getDamagedStockQuantity(
            @RequestParam Long productId,
            @RequestParam Long locationId) {
        return ResponseEntity.ok(inventoryService.getDamagedStockQuantity(productId, locationId));
    }

    @GetMapping("/{id}/quantities")
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<InventoryQuantityResponseDto> getQuantitiesById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getQuantitiesById(id));
    }

    @PostMapping("/increase")
    @PreAuthorize("hasAuthority('inventory.update')")
    public ResponseEntity<Void> increaseStock(
            @RequestParam Long productId,
            @RequestParam Long locationId,
            @RequestParam Integer quantity) {
        inventoryService.increaseStock(productId, locationId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decrease")
    @PreAuthorize("hasAuthority('inventory.update')")
    public ResponseEntity<Void> decreaseStock(
            @RequestParam Long productId,
            @RequestParam Long locationId,
            @RequestParam Integer quantity) {
        inventoryService.decreaseStock(productId, locationId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reserve")
    @PreAuthorize("hasAuthority('inventory.update')")
    public ResponseEntity<Void> reserveStock(
            @RequestParam Long productId,
            @RequestParam Long locationId,
            @RequestParam Integer quantity) {
        inventoryService.reserveStock(productId, locationId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/release-reserved")
    @PreAuthorize("hasAuthority('inventory.update')")
    public ResponseEntity<Void> releaseReservedStock(
            @RequestParam Long productId,
            @RequestParam Long locationId,
            @RequestParam Integer quantity) {
        inventoryService.releaseReservedStock(productId, locationId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-damaged")
    @PreAuthorize("hasAuthority('inventory.update')")
    public ResponseEntity<Void> markAsDamaged(
            @RequestParam Long productId,
            @RequestParam Long locationId,
            @RequestParam Integer quantity) {
        inventoryService.markAsDamaged(productId, locationId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/write-off-damaged")
    @PreAuthorize("hasAuthority('inventory.update')")
    public ResponseEntity<Void> writeDamagedOff(
            @RequestParam Long productId,
            @RequestParam Long locationId,
            @RequestParam Integer quantity) {
        inventoryService.writeDamagedOff(productId, locationId, quantity);
        return ResponseEntity.ok().build();
    }
}