package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.partner.CreatePartnerDto;
import az.microservice.werehouseapplication.model.dto.request.partner.UpdatePartnerDto;
import az.microservice.werehouseapplication.model.dto.response.partner.PartnerResponseDto;
import az.microservice.werehouseapplication.service.Interface.IPartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
public class PartnerController {

    private final IPartnerService vendorService;

    @PostMapping
    @PreAuthorize("hasAuthority('inbound.create')")
    public ResponseEntity<PartnerResponseDto> create(@Valid @RequestBody CreatePartnerDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendorService.create(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('inbound.view')")
    public ResponseEntity<List<PartnerResponseDto>> getAll() {
        return ResponseEntity.ok(vendorService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inbound.view')")
    public ResponseEntity<PartnerResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('inbound.update')")
    public ResponseEntity<PartnerResponseDto> update(@PathVariable Long id,
                                                    @RequestBody UpdatePartnerDto dto) {
        return ResponseEntity.ok(vendorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('inbound.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vendorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}