package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.exception.AlreadyExistException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.model.dto.request.brand.CreateBrandDto;
import az.microservice.werehouseapplication.model.dto.request.brand.UpdateBrandDto;
import az.microservice.werehouseapplication.model.dto.response.brand.BrandResponseDto;
import az.microservice.werehouseapplication.model.entity.product.Brand;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import az.microservice.werehouseapplication.repository.BrandRepository;
import az.microservice.werehouseapplication.repository.OrganizationRepository;
import az.microservice.werehouseapplication.repository.UserRepository;
import az.microservice.werehouseapplication.service.Interface.IBrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandService implements IBrandService {

    private final BrandRepository brandRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public BrandResponseDto create(CreateBrandDto dto) {

        Organization organization = organizationRepository.findById(dto.getOrganizationId())
                .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));

        Optional<Brand> inactiveBrand = brandRepository
                .findByNameAndOrganizationAndStatus(dto.getName(), organization, ItemStatus.INACTIVE);

        if(inactiveBrand.isPresent()) {
            Brand brand = inactiveBrand.get();
            brand.setStatus(ItemStatus.ACTIVE);
            brand.setDescription(dto.getDescription());
            return toResponse(brandRepository.save(brand));
        }

        if (brandRepository.existsByNameAndOrganization(dto.getName(), organization)) {
            throw new AlreadyExistException(BRAND_ALREADY_EXIST.getMessage());
        }

        if(brandRepository.existsByName(dto.getName())){
            throw new AlreadyExistException(BRAND_ALREADY_EXIST.getMessage());
        }

        Brand brand = Brand.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .organization(organization)
                .build();

        brandRepository.save(brand);
        return toResponse(brand);
    }

    @Override
    public BrandResponseDto getById(Long id) {
        log.info("Fetching brand with id: {}", id);
        return toResponse(findActiveBrandById(id));
    }

    @Override
    public List<BrandResponseDto> getAll() {
        log.info("Fetching all brands");

        return brandRepository.findAllByStatus(ItemStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();

    }

    public List<BrandResponseDto> getAllByOrganization(Long organizationId){
        log.info("Fetching organization's brands");

        return brandRepository.findAllByOrganizationIdAndStatus(
                        organizationId, ItemStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public BrandResponseDto update(Long id, UpdateBrandDto dto) {
        log.info("Updating brand with id: {}", id);
        Brand brand = findActiveBrandById(id);

        if (dto.getName() != null && !dto.getName().equals(brand.getName())) {
            if (brandRepository.existsByNameAndOrganization(dto.getName(), brand.getOrganization())) {
                throw new AlreadyExistException(BRAND_ALREADY_EXIST.getMessage());
            }
            brand.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            brand.setDescription(dto.getDescription());
        }

        return toResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting brand with id: {}", id);
        Brand brand = findActiveBrandById(id);

        brand.setStatus(ItemStatus.INACTIVE);
        brandRepository.save(brand);
    }

    public Brand getBrandEntityById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(BRAND_NOT_FOUND.getMessage()));
    }


    // region Private helper methods

    private Brand findActiveBrandById(Long id) {
        return brandRepository.findByIdAndStatus(id, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(BRAND_NOT_FOUND.getMessage()));
    }

    private BrandResponseDto toResponse(Brand brand) {
        return BrandResponseDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .organizationName(brand.getOrganization().getName())
                .build();
    }

}
