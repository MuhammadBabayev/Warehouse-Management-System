package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.organization.CreateOrganizationDto;
import az.microservice.werehouseapplication.model.dto.request.organization.UpdateOrganizationDto;
import az.microservice.werehouseapplication.model.dto.response.organization.OrganizationResponseDto;
import az.microservice.werehouseapplication.service.Interface.IOrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final IOrganizationService organizationService;

    @PostMapping
    @PreAuthorize("hasAuthority('organization.create')")
    public ResponseEntity<OrganizationResponseDto> create(@Valid @RequestBody CreateOrganizationDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('organization.view')")
    public ResponseEntity<List<OrganizationResponseDto>> getAll() {
        return ResponseEntity.ok(organizationService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('organization.view')")
    public ResponseEntity<OrganizationResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('organization.update')")
    public ResponseEntity<OrganizationResponseDto> update(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateOrganizationDto request) {
        return ResponseEntity.ok(organizationService.update(id, request));
    }

    //Soft Deleting-dir
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('organization.update')")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id) {
        organizationService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
