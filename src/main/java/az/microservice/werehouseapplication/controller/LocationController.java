package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.location.CreateLocationDto;
import az.microservice.werehouseapplication.model.dto.request.location.UpdateLocationDto;
import az.microservice.werehouseapplication.model.dto.request.location.UpdateLocationStatusDto;
import az.microservice.werehouseapplication.model.dto.response.location.LocationResponseDto;
import az.microservice.werehouseapplication.service.Interface.ILocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final ILocationService locationService;

    @PostMapping
    @PreAuthorize("hasAuthority('warehouse.create')")
    public ResponseEntity<LocationResponseDto> create(@Valid @RequestBody CreateLocationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.create(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('warehouse.view')")
    public ResponseEntity<List<LocationResponseDto>> getAll() {
        return ResponseEntity.ok(locationService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse.view')")
    public ResponseEntity<LocationResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getById(id));
    }

    @GetMapping("/shelf/{shelfId}")
    @PreAuthorize("hasAuthority('warehouse.view')")
    public ResponseEntity<List<LocationResponseDto>> getAllByShelfId(@PathVariable Long shelfId) {
        return ResponseEntity.ok(locationService.getAllByShelfId(shelfId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse.update')")
    public ResponseEntity<LocationResponseDto> update(@PathVariable Long id,
                                                      @RequestBody UpdateLocationDto dto) {
        return ResponseEntity.ok(locationService.update(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('warehouse.update')")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,
                                             @Valid @RequestBody UpdateLocationStatusDto dto) {
        locationService.updateStatus(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}