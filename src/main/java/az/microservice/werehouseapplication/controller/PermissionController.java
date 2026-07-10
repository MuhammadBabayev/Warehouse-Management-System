package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.permission.CreatePermissionDto;
import az.microservice.werehouseapplication.model.dto.request.permission.UpdatePermissionDto;
import az.microservice.werehouseapplication.model.dto.response.permission.PermissionResponseDto;
import az.microservice.werehouseapplication.service.Interface.IPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final IPermissionService permissionService;


    @PostMapping
    @PreAuthorize("hasAuthority('role.create')")
    public ResponseEntity<PermissionResponseDto> create(@Valid @RequestBody CreatePermissionDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.createPermission(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role.view')")
    public ResponseEntity<List<PermissionResponseDto>> getAll() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role.view')")
    public ResponseEntity<PermissionResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role.update')")
    public ResponseEntity<PermissionResponseDto> update(@PathVariable Long id,
                                                        @RequestBody UpdatePermissionDto request) {
        return ResponseEntity.ok(permissionService.updatePermissions(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role.create')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
