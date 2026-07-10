package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.role.CreateRoleDto;
import az.microservice.werehouseapplication.model.dto.request.role.UpdateRoleDto;
import az.microservice.werehouseapplication.model.dto.response.role.RoleResponseDto;

import java.util.List;

public interface IRoleService {
    RoleResponseDto create(CreateRoleDto request);
    RoleResponseDto getById(Long id);
    List<RoleResponseDto> getAll();
    List<String> getPermissions(Long roleId);
    RoleResponseDto update(Long id, UpdateRoleDto request);
    void delete(Long id);
    void addPermission(Long roleId, Long permissionId);
    void removePermission(Long roleId, Long permissionId);
}
