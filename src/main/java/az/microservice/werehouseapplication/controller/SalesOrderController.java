package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.outbound.CreateSalesOrderDto;
import az.microservice.werehouseapplication.model.dto.request.outbound.CreateSalesOrderItemDto;
import az.microservice.werehouseapplication.model.dto.response.outbound.SalesOrderResponseDto;
import az.microservice.werehouseapplication.service.Interface.ISalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final ISalesOrderService salesOrderService;

    @PostMapping
    @PreAuthorize("hasAuthority('outbound.create')")
    public ResponseEntity<SalesOrderResponseDto> create(@Valid @RequestBody CreateSalesOrderDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salesOrderService.create(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('outbound.view')")
    public ResponseEntity<List<SalesOrderResponseDto>> getAll() {
        return ResponseEntity.ok(salesOrderService.getAll());
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('outbound.view')")
    public ResponseEntity<List<SalesOrderResponseDto>> getAllByOrganizationId(@PathVariable Long organizationId){
        return ResponseEntity.ok(salesOrderService.getAllByOrganizationId(organizationId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('outbound.view')")
    public ResponseEntity<SalesOrderResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(salesOrderService.getById(id));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('outbound.update')")
    public ResponseEntity<Void> confirm(@PathVariable Long id) {
        salesOrderService.confirm(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('outbound.update')")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        salesOrderService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasAuthority('outbound.create')")
    public ResponseEntity<Void> addItem(@PathVariable Long id,
                                        @Valid @RequestBody CreateSalesOrderItemDto dto) {
        salesOrderService.addItem(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @PreAuthorize("hasAuthority('outbound.delete')")
    public ResponseEntity<Void> removeItem(@PathVariable Long id,
                                           @PathVariable Long itemId) {
        salesOrderService.removeItem(id, itemId);
        return ResponseEntity.noContent().build();
    }
}