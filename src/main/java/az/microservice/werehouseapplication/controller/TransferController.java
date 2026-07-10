package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.transfer.TransferItemRequestDto;
import az.microservice.werehouseapplication.model.dto.request.transfer.TransferRequestDto;
import az.microservice.werehouseapplication.model.dto.response.transfer.TransferResponseDto;
import az.microservice.werehouseapplication.service.Interface.ITransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final ITransferService transferService;

    @PostMapping
    @PreAuthorize("hasAuthority('transfer.create')")
    public ResponseEntity<TransferResponseDto> create(@RequestBody @Valid TransferRequestDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transferService.create(dto));
    }

    @PatchMapping("/{id}/ship")
    @PreAuthorize("hasAuthority('transfer.update')")
    public ResponseEntity<TransferResponseDto> ship(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.ship(id));
    }

    @PatchMapping("/{id}/receive")
    @PreAuthorize("hasAuthority('transfer.update')")
    public ResponseEntity<TransferResponseDto> receive(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.receive(id));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('transfer.update')")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        transferService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{transferId}/items")
    @PreAuthorize("hasAuthority('transfer.update')")
    public ResponseEntity<Void> addItem(
            @PathVariable Long transferId,
            @RequestBody @Valid TransferItemRequestDto dto) {
        transferService.addItem(transferId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{transferId}/items/{itemId}")
    @PreAuthorize("hasAuthority('transfer.update')")
    public ResponseEntity<Void> updateItemQuantity(
            @PathVariable Long transferId,
            @PathVariable Long itemId,
            @RequestParam Integer newQuantity) {
        transferService.updateItemQuantity(transferId, itemId, newQuantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{transferId}/items/{itemId}")
    @PreAuthorize("hasAuthority('transfer.delete')")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long transferId,
            @PathVariable Long itemId) {
        transferService.removeItem(transferId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('transfer.view')")
    public ResponseEntity<TransferResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('transfer.view')")
    public ResponseEntity<List<TransferResponseDto>> getAll() {
        return ResponseEntity.ok(transferService.getAll());
    }
}