package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.permission.CreatePermissionDto;
import az.microservice.werehouseapplication.model.dto.request.permission.UpdatePermissionDto;
import az.microservice.werehouseapplication.model.dto.response.permission.PermissionResponseDto;

import java.util.List;

public interface IPermissionService {
    PermissionResponseDto createPermission(CreatePermissionDto request);
    PermissionResponseDto getPermissionById(Long id);
    List<PermissionResponseDto> getAllPermissions();
    PermissionResponseDto updatePermissions(Long id, UpdatePermissionDto request);
    void deletePermission(Long id);
}
