package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.response.transfer.StockMovementResponseDto;
import az.microservice.werehouseapplication.service.Implementation.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<List<StockMovementResponseDto>> getMovementsByProduct(
            @PathVariable Long productId) {
        return ResponseEntity.ok(stockMovementService.getMovementsByProduct(productId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<List<StockMovementResponseDto>> getMovementsByWarehouse(
            @PathVariable Long warehouseId) {
        return ResponseEntity.ok(stockMovementService.getMovementsByWarehouse(warehouseId));
    }

//    @GetMapping("/reference")
//    @PreAuthorize("hasAuthority('inventory.view')")
//    public ResponseEntity<List<StockMovementResponseDto>> getMovementsByReference(
//            @RequestParam String referenceType,
//            @RequestParam Long referenceId) {
//        return ResponseEntity.ok(stockMovementService.getMovementsByReference(referenceType, referenceId));
//    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<List<StockMovementResponseDto>> getMovementsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(stockMovementService.getMovementsByDateRange(from, to));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<List<StockMovementResponseDto>> getAll() {
        return ResponseEntity.ok(stockMovementService.getAll());
    }

//    @GetMapping("/product/{productId}")
//    @PreAuthorize("hasAuthority('inventory.view')")
//    public ResponseEntity<List<StockMovementResponseDto>> getAllByProductId(@PathVariable Long productId) {
//        return ResponseEntity.ok(stockMovementService.getAllByProductId(productId));
//    }

    @GetMapping("/location/{locationId}")
    @PreAuthorize("hasAuthority('inventory.view')")
    public ResponseEntity<List<StockMovementResponseDto>> getAllByLocationId(@PathVariable Long locationId) {
        return ResponseEntity.ok(stockMovementService.getAllByLocationId(locationId));
    }
}
