package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.purchaseOrder.CreatePurchaseOrderDto;
import az.microservice.werehouseapplication.model.dto.request.purchaseOrder.CreatePurchaseOrderItemDto;
import az.microservice.werehouseapplication.model.dto.response.purchaseOrder.PurchaseOrderResponseDto;
import az.microservice.werehouseapplication.service.Interface.IPurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final IPurchaseOrderService purchaseOrderService;

    @PostMapping
    @PreAuthorize("hasAuthority('inbound.create')")
    public ResponseEntity<PurchaseOrderResponseDto> create(@Valid @RequestBody CreatePurchaseOrderDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.create(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('inbound.view')")
    public ResponseEntity<List<PurchaseOrderResponseDto>> getAll() {
        return ResponseEntity.ok(purchaseOrderService.getAll());
    }

    @GetMapping("/organization/{id}")
    @PreAuthorize("hasAuthority('inbound.view')")
    public ResponseEntity<List<PurchaseOrderResponseDto>> getAllByOrganizationId(@PathVariable Long id){
        return ResponseEntity.ok(purchaseOrderService.getAllByOrganizationId(id));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inbound.view')")
    public ResponseEntity<PurchaseOrderResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getById(id));
    }


    @PostMapping("/{id}/items")
    @PreAuthorize("hasAuthority('inbound.create')")
    public ResponseEntity<Void> addItem(@PathVariable Long id,
                                        @Valid @RequestBody CreatePurchaseOrderItemDto dto) {
        purchaseOrderService.addItem(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @PreAuthorize("hasAuthority('inbound.create')")
    public ResponseEntity<Void> removeItem(@PathVariable Long id,
                                           @PathVariable Long itemId) {
        purchaseOrderService.removeItem(id, itemId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('inbound.update')")
    public ResponseEntity<Void> confirmPurchaseOrderStatusFromDraft(@PathVariable Long id) {
        purchaseOrderService.confirmPurchaseOrderStatusFromDraft(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('inbound.update')")
    public ResponseEntity<Void> cancelPurchaseOrderStatus(@PathVariable Long id) {
        purchaseOrderService.cancelPurchaseOrderStatus(id);
        return ResponseEntity.noContent().build();
    }}
