package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.exception.AlreadyExistException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.model.dto.request.role.CreateRoleDto;
import az.microservice.werehouseapplication.model.dto.request.role.UpdateRoleDto;
import az.microservice.werehouseapplication.model.dto.response.role.RoleResponseDto;
import az.microservice.werehouseapplication.model.entity.users.Permissions;
import az.microservice.werehouseapplication.model.entity.users.Role;
import az.microservice.werehouseapplication.model.entity.users.RolePermission;
import az.microservice.werehouseapplication.repository.PermissionRepository;
import az.microservice.werehouseapplication.repository.RolePermissonRepository;
import az.microservice.werehouseapplication.repository.RoleRepository;
import az.microservice.werehouseapplication.service.Interface.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissonRepository rolePermissionRepository;

    @Override
    @Transactional
    public RoleResponseDto create(CreateRoleDto request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new AlreadyExistException(ROLE_ALREADY_EXIST.getMessage());
        }

        Optional<Role> inactiveRole = roleRepository.findByName(request.getName());

        if(inactiveRole.isPresent()){
            Role role = inactiveRole.get();
            role.setStatus(ItemStatus.ACTIVE);
            return toResponse(roleRepository.save(role));
        }

        Role role = Role.builder()
                .name(request.getName())
                .build();

        return toResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponseDto getById(Long id) {
        Role role = findRoleById(id);
        if (role.getStatus() == ItemStatus.INACTIVE) {
            throw new NotFoundException(ROLE_NOT_FOUND.getMessage());
        }
        return toResponse(role);
    }

    @Override
    public List<RoleResponseDto> getAll() {
        return roleRepository.findAllByStatus(ItemStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public RoleResponseDto update(Long id, UpdateRoleDto request) {
        Role role = findRoleById(id);

        if (role.getStatus() == ItemStatus.INACTIVE) {
            throw new NotFoundException(ROLE_NOT_FOUND.getMessage());
        }

        if (!role.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
            throw new AlreadyExistException(ROLE_ALREADY_EXIST.getMessage());
        }

        role.setName(request.getName());
        return toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Role role = findRoleById(id);
        role.setStatus(ItemStatus.INACTIVE);
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void addPermission(Long roleId, Long permissionId) {
        Role role = findRoleById(roleId);
        Permissions permission = findPermissionById(permissionId);

        if (role.getStatus() == ItemStatus.INACTIVE) {
            throw new NotFoundException(ROLE_NOT_FOUND.getMessage());
        }

        if (permission.getStatus() == ItemStatus.INACTIVE) {
            throw new NotFoundException(PERMISSION_NOT_FOUND.getMessage());
        }

        if (rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId)) {
            throw new AlreadyExistException(PERMISSION_ALREADY_ASSIGNED_TO_ROLE.getMessage());
        }

        rolePermissionRepository.save(
                RolePermission.builder()
                        .role(role)
                        .permission(permission)
                        .build()
        );
    }

    @Override
    @Transactional
    public void removePermission(Long roleId, Long permissionId) {
        RolePermission rolePermission = rolePermissionRepository
                .findByRoleIdAndPermissionId(roleId, permissionId)
                .orElseThrow(() -> new NotFoundException(PERMISSION_NOT_FOUND_IN_THIS_ROLE.getMessage()));

        if (rolePermission.getStatus() == ItemStatus.INACTIVE) {
            throw new NotFoundException(PERMISSION_NOT_FOUND_IN_THIS_ROLE.getMessage());
        }

        rolePermission.setStatus(ItemStatus.INACTIVE);
        rolePermissionRepository.save(rolePermission);
    }

    @Override
    public List<String>  getPermissions(Long id) {
        Role role = findRoleById(id);

        if (role.getStatus() == ItemStatus.INACTIVE) {
            throw new NotFoundException(ROLE_NOT_FOUND.getMessage());
        }

        return rolePermissionRepository.findByRoleIdAndStatus(role.getId(), ItemStatus.ACTIVE)
                .stream()
                .map(rp -> rp.getPermission().getName())
                .collect(Collectors.toList());
    }

    // HELPER METHODS

    private Role findRoleById(Long id) {
        return roleRepository.findByIdAndStatus(id, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND.getMessage()));
    }

    private Permissions findPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PERMISSION_NOT_FOUND.getMessage()));
    }

    private RoleResponseDto toResponse(Role role) {
        List<String> permissions = rolePermissionRepository
                .findByRoleIdAndStatus(role.getId(), ItemStatus.ACTIVE)
                .stream()
                .map(rp -> rp.getPermission().getName())
                .collect(Collectors.toList());

        return RoleResponseDto.builder()
                .id(role.getId())
                .name(role.getName())
                .permissions(permissions)
                .build();
    }

}
