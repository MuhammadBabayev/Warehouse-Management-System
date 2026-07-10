package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.exception.AlreadyExistException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.model.dto.request.permission.CreatePermissionDto;
import az.microservice.werehouseapplication.model.dto.request.permission.UpdatePermissionDto;
import az.microservice.werehouseapplication.model.dto.response.permission.PermissionResponseDto;
import az.microservice.werehouseapplication.model.entity.users.Permissions;
import az.microservice.werehouseapplication.repository.PermissionRepository;
import az.microservice.werehouseapplication.service.Interface.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.PERMISSION_ALREADY_EXIST;
import static az.microservice.werehouseapplication.exception.ExceptionMessage.PERMISSION_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;


    @Override
    @Transactional
    public PermissionResponseDto createPermission(CreatePermissionDto request){

        if(permissionRepository.existsByName(request.getName())){
            throw new AlreadyExistException(PERMISSION_ALREADY_EXIST.getMessage());
        }
        Permissions permission = Permissions.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return toResponse(permissionRepository.save(permission));
    }

    @Override
    public PermissionResponseDto getPermissionById(Long id) {
        Permissions permission=FindPermissionById(id);
        if(permission.getStatus()== ItemStatus.INACTIVE){
            throw new NotFoundException(PERMISSION_NOT_FOUND.getMessage());
        }

        return toResponse(permission);
    }

    @Override
    public List<PermissionResponseDto> getAllPermissions() {
        return permissionRepository.findAllByStatus(ItemStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    public PermissionResponseDto updatePermissions(Long id, UpdatePermissionDto request) {
        Permissions permission = FindPermissionById(id);

        if (permission.getStatus() == ItemStatus.INACTIVE) {
            throw new NotFoundException(PERMISSION_NOT_FOUND.getMessage());
        }
        if (request.getName() != null && !permission.getName().equals(request.getName())) {
            if (permissionRepository.existsByName(request.getName())) {
                throw new AlreadyExistException(PERMISSION_ALREADY_EXIST.getMessage());
            }
            permission.setName(request.getName());
        }

            permission.setDescription(request.getDescription());

        return toResponse(permissionRepository.save(permission));
    }

    @Override
    public void deletePermission(Long id) {
        Permissions permission = FindPermissionById(id);

        if (permission.getStatus() == ItemStatus.INACTIVE) {
            throw new NotFoundException(PERMISSION_NOT_FOUND.getMessage());
        }

        permission.setStatus(ItemStatus.INACTIVE);
        permissionRepository.save(permission);
    }


    //region Helper methods
    private Permissions FindPermissionById(Long id){
        return permissionRepository.findByIdAndStatus(id, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(PERMISSION_NOT_FOUND.getMessage()));
    }

    private PermissionResponseDto toResponse(Permissions permission){
        return PermissionResponseDto.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }

}
