package az.microservice.werehouseapplication.controller;


import az.microservice.werehouseapplication.model.dto.response.outbound.PickingResponseDto;
import az.microservice.werehouseapplication.service.Interface.IPickingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pickings")
@RequiredArgsConstructor
public class PickingController {

    private final IPickingService pickingService;

//    @PostMapping
//    @PreAuthorize("hasAuthority('outbound.create')")
//    public ResponseEntity<PickingResponseDto> create(@Valid @RequestBody CreatePickingDto dto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(pickingService.create(dto));
//    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('outbound.view')")
    public ResponseEntity<PickingResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(pickingService.getById(id));
    }

    @GetMapping("/sales-order/{salesOrderId}")
    @PreAuthorize("hasAuthority('outbound.view')")
    public ResponseEntity<List<PickingResponseDto>> getAllBySalesOrderId(@PathVariable Long salesOrderId) {
        return ResponseEntity.ok(pickingService.getAllBySalesOrderId(salesOrderId));
    }

//    @PatchMapping("/{id}/start")
//    @PreAuthorize("hasAuthority('outbound.update')")
//    public ResponseEntity<Void> start(@PathVariable Long id) {
//        pickingService.start(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/{id}/items/complete")
//    @PreAuthorize("hasAuthority('outbound.update')")
//    public ResponseEntity<Void> completeItem(@PathVariable Long id,
//                                             @Valid @RequestBody CompletePickingItemDto dto) {
//        pickingService.completeItem(id, dto);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/{id}/complete")
//    @PreAuthorize("hasAuthority('outbound.update')")
//    public ResponseEntity<Void> complete(@PathVariable Long id) {
//        pickingService.complete(id);
//        return ResponseEntity.noContent().build();
//    }
}