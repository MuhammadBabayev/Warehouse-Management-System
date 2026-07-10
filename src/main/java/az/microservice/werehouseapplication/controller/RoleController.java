package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.role.CreateRoleDto;
import az.microservice.werehouseapplication.model.dto.request.role.UpdateRoleDto;
import az.microservice.werehouseapplication.model.dto.response.role.RoleResponseDto;
import az.microservice.werehouseapplication.service.Interface.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final IRoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('role.create')")
    public ResponseEntity<RoleResponseDto> create(@Valid @RequestBody CreateRoleDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role.view')")
    public ResponseEntity<List<RoleResponseDto>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role.view')")
    public ResponseEntity<RoleResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role.update')")
    public ResponseEntity<RoleResponseDto> update(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateRoleDto request) {
        return ResponseEntity.ok(roleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role.create')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('role.update')")
    public ResponseEntity<Void> addPermission(@PathVariable Long id,
                                              @PathVariable Long permissionId) {
        roleService.addPermission(id, permissionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('role.update')")
    public ResponseEntity<Void> removePermission(@PathVariable Long id,
                                                 @PathVariable Long permissionId) {
        roleService.removePermission(id, permissionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role.view')")
    public ResponseEntity<List<String>> getPermissions(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.getPermissions(roleId));
    }
}
