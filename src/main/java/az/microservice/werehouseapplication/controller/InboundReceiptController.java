package az.microservice.werehouseapplication.controller;



import az.microservice.werehouseapplication.model.dto.request.inboundReceipt.CreateInboundReceiptDto;
import az.microservice.werehouseapplication.model.dto.response.inboundReceipt.InboundReceiptResponseDto;
import az.microservice.werehouseapplication.service.Interface.IInboundReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inbound-receipts")
@RequiredArgsConstructor
public class InboundReceiptController {

    private final IInboundReceiptService inboundReceiptService;

    @PostMapping
    @PreAuthorize("hasAuthority('inbound.create')")
    public ResponseEntity<InboundReceiptResponseDto> create(@Valid @RequestBody CreateInboundReceiptDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inboundReceiptService.create(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('inbound.view')")
    public ResponseEntity<List<InboundReceiptResponseDto>> getAll() {
        return ResponseEntity.ok(inboundReceiptService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inbound.view')")
    public ResponseEntity<InboundReceiptResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inboundReceiptService.getById(id));
    }

    @GetMapping("/purchase-order/{purchaseOrderId}")
    @PreAuthorize("hasAuthority('inbound.view')")
    public ResponseEntity<List<InboundReceiptResponseDto>> getAllByPurchaseOrderId(
            @PathVariable Long purchaseOrderId) {
        return ResponseEntity.ok(inboundReceiptService.getAllByPurchaseOrderId(purchaseOrderId));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('inbound.update')")
    public ResponseEntity<Void> complete(@PathVariable Long id) {
        inboundReceiptService.complete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('inbound.update')")
    public ResponseEntity<Void> reject(@PathVariable Long id) {
        inboundReceiptService.reject(id);
        return ResponseEntity.noContent().build();
    }
}