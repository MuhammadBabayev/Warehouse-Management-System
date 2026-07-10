package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.brand.CreateBrandDto;
import az.microservice.werehouseapplication.model.dto.request.brand.UpdateBrandDto;
import az.microservice.werehouseapplication.model.dto.response.brand.BrandResponseDto;

import java.util.List;

public interface IBrandService {
    BrandResponseDto create(CreateBrandDto dto);
    BrandResponseDto getById(Long id);
    List<BrandResponseDto> getAll();
    BrandResponseDto update(Long id, UpdateBrandDto dto);
    void delete(Long id);
    List<BrandResponseDto> getAllByOrganization(Long organizationId);
}
