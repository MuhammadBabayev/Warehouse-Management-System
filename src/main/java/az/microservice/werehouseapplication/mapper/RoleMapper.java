package az.microservice.werehouseapplication.mapper;

import az.microservice.werehouseapplication.model.dto.request.role.CreateRoleDto;
import az.microservice.werehouseapplication.model.dto.response.role.RoleResponseDto;
import az.microservice.werehouseapplication.model.entity.users.Role;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RoleMapper {
    RoleResponseDto toResponseDto(Role role);

    @Mapping(target = "id", ignore = true)
    Role toEntity(CreateRoleDto dto);

    List<RoleResponseDto> toResponseDtoList(List<Role> roles);

//    void updateRoleFromDto(UpdateRoleDto dto, @MappingTarget Role role);
}
