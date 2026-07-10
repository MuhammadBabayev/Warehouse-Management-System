package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.shelf.CreateShelfDto;
import az.microservice.werehouseapplication.model.dto.request.shelf.UpdateShelfDto;
import az.microservice.werehouseapplication.model.dto.response.shelf.ShelfResponseDto;
import az.microservice.werehouseapplication.service.Interface.IShelfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shelf")
@RequiredArgsConstructor
public class ShelfController {
    private final IShelfService shelfService;

    @PostMapping
    @PreAuthorize("hasAuthority('warehouse.create')")
    public ResponseEntity<ShelfResponseDto> createShelf(@RequestBody @Valid CreateShelfDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shelfService.createShelf(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse.view')")
    public ShelfResponseDto getShelfById(@PathVariable Long id) {
        return shelfService.getShelfById(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('warehouse.view')")
    public List<ShelfResponseDto> getAllShelf() {
        return shelfService.getAllShelf();
    }

    @GetMapping("/zone/{zoneId}")
    @PreAuthorize("hasAuthority('warehouse.view')")
    public List<ShelfResponseDto> getAllByZoneId(@PathVariable Long zoneId) {
        return shelfService.getAllByZoneId(zoneId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse.update')")
    public ResponseEntity<ShelfResponseDto> updateShelf(@PathVariable Long id,
                                                   @RequestBody @Valid UpdateShelfDto request) {
        return ResponseEntity.ok(shelfService.updateShelf(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse.delete')")
    public ResponseEntity<Void> deleteShelf(@PathVariable Long id) {
        shelfService.deleteShelf(id);
        return ResponseEntity.noContent().build();
    }
}
