package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.organization.CreateOrganizationDto;
import az.microservice.werehouseapplication.model.dto.request.organization.UpdateOrganizationDto;
import az.microservice.werehouseapplication.model.dto.response.organization.OrganizationResponseDto;

import java.util.List;

public interface IOrganizationService {
    OrganizationResponseDto create(CreateOrganizationDto request);
    OrganizationResponseDto getById(Long id);
    List<OrganizationResponseDto> getAll();
    OrganizationResponseDto update(Long id, UpdateOrganizationDto request);
    void delete(Long id);
}
