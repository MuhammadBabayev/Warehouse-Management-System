package az.microservice.werehouseapplication.mapper;

import az.microservice.werehouseapplication.model.dto.request.organization.CreateOrganizationDto;
import az.microservice.werehouseapplication.model.dto.request.organization.UpdateOrganizationDto;
import az.microservice.werehouseapplication.model.dto.response.organization.OrganizationResponseDto;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrganizationMapper {

    @Mapping(target = "id", ignore = true)
    Organization toEntity(CreateOrganizationDto dto);

    List<OrganizationResponseDto> toResponseDtoList(List<Organization> organization);

    OrganizationResponseDto toResponseDto(Organization organization);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) //Bu hisse hansi column update olunmasin deye deyer oturmesek null getmesin deyedi
    void updateEntity(UpdateOrganizationDto dto, @MappingTarget Organization organization);
}
