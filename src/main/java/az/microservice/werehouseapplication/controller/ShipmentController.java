package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.response.outbound.ShipmentResponseDto;
import az.microservice.werehouseapplication.service.Interface.IShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final IShipmentService shipmentService;

//    @PostMapping
//    @PreAuthorize("hasAuthority('outbound.create')")
//    public ResponseEntity<ShipmentResponseDto> create(@Valid @RequestBody CreateShipmentDto dto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(shipmentService.create(dto));
//    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('outbound.view')")
    public ResponseEntity<ShipmentResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getById(id));
    }

    @GetMapping("/sales-order/{salesOrderId}")
    @PreAuthorize("hasAuthority('outbound.view')")
    public ResponseEntity<List<ShipmentResponseDto>> getAllBySalesOrderId(@PathVariable Long salesOrderId) {
        return ResponseEntity.ok(shipmentService.getAllBySalesOrderId(salesOrderId));
    }

//    @PatchMapping("/{id}/ship")
//    @PreAuthorize("hasAuthority('shipment.update')")
//    public ResponseEntity<Void> ship(@PathVariable Long id) {
//        shipmentService.ship(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/{id}/deliver")
//    @PreAuthorize("hasAuthority('shipment.update')")
//    public ResponseEntity<Void> deliver(@PathVariable Long id) {
//        shipmentService.deliver(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/{id}/return")
//    @PreAuthorize("hasAuthority('shipment.update')")
//    public ResponseEntity<Void> returnShipment(@PathVariable Long id) {
//        shipmentService.returnShipment(id);
//        return ResponseEntity.noContent().build();
//    }
}