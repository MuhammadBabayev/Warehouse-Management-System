package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.brand.CreateBrandDto;
import az.microservice.werehouseapplication.model.dto.request.brand.UpdateBrandDto;
import az.microservice.werehouseapplication.model.dto.response.brand.BrandResponseDto;
import az.microservice.werehouseapplication.service.Interface.IBrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final IBrandService brandService;

    @PostMapping
    @PreAuthorize("hasAuthority('product.create')")
    public ResponseEntity<BrandResponseDto> create(@Valid @RequestBody CreateBrandDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.create(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('product.view')")
    public ResponseEntity<List<BrandResponseDto>> getAll() {
        return ResponseEntity.ok(brandService.getAll());
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('product.view')")
    public ResponseEntity<List<BrandResponseDto>> getAllByOrganization(@PathVariable Long organizationId){
        return ResponseEntity.ok(brandService.getAllByOrganization(organizationId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('product.view')")
    public ResponseEntity<BrandResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product.update')")
    public ResponseEntity<BrandResponseDto> update(@PathVariable Long id,
                                                   @RequestBody UpdateBrandDto dto) {
        return ResponseEntity.ok(brandService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}