package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.zone.CreateZoneDto;
import az.microservice.werehouseapplication.model.dto.request.zone.UpdateZoneDto;
import az.microservice.werehouseapplication.model.dto.response.zone.ZoneResponseDto;
import az.microservice.werehouseapplication.service.Interface.IZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/zone")
@RequiredArgsConstructor
public class ZoneController {
    private final IZoneService zoneService;

    @PostMapping
    @PreAuthorize("hasAuthority('warehouse.create')")
    public ResponseEntity<ZoneResponseDto> createZone(@RequestBody @Valid CreateZoneDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(zoneService.createZone(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse.view')")
    public ZoneResponseDto getZoneById(@PathVariable Long id) {
        return zoneService.getZoneById(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('warehouse.view')")
    public List<ZoneResponseDto> getAllZone() {
        return zoneService.getAllZone();
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAuthority('warehouse.view')")
    public List<ZoneResponseDto> getAllByWarehouseId(@PathVariable Long warehouseId) {
        return zoneService.getAllByWarehouseId(warehouseId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse.update')")
    public ResponseEntity<ZoneResponseDto> updateZone(@PathVariable Long id,
                                                  @RequestBody @Valid UpdateZoneDto request) {
        return ResponseEntity.ok(zoneService.updateZone(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse.delete')")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }
}
