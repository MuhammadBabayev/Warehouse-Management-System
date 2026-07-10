package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.LocationStatus;
import az.microservice.werehouseapplication.exception.AlreadyExistException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.mapper.WarehouseMapper;
import az.microservice.werehouseapplication.model.dto.request.warehouse.ChangeWarehouseOrganizationDto;
import az.microservice.werehouseapplication.model.dto.request.warehouse.CreateWarehouseDto;
import az.microservice.werehouseapplication.model.dto.request.warehouse.UpdateWarehouseDto;
import az.microservice.werehouseapplication.model.dto.response.warehouse.WarehouseResponseDto;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import az.microservice.werehouseapplication.model.entity.warehouse.Warehouse;
import az.microservice.werehouseapplication.repository.OrganizationRepository;
import az.microservice.werehouseapplication.repository.WarehouseRepository;
import az.microservice.werehouseapplication.service.Interface.IWarehouseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
public class WarehouseService implements IWarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public WarehouseResponseDto createWarehouse(CreateWarehouseDto dto) {

        Organization organization = organizationRepository.findByIdAndStatus(dto.getOrganizationId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));

        if (organization == null) throw new NotFoundException(ORGANIZATION_NOT_FOUND.getMessage());

        Optional<Warehouse> inactiveWarehouse = warehouseRepository
                .findByNameAndOrganizationAndStatus(dto.getName(), organization, ItemStatus.INACTIVE);

        if (inactiveWarehouse.isPresent()) {
            Warehouse warehouse = inactiveWarehouse.get();
            warehouse.setStatus(ItemStatus.ACTIVE);
            warehouse.setAddress(dto.getAddress());
            warehouse.setPhone(dto.getPhone());
            return warehouseMapper.toDto(warehouseRepository.save(warehouse));
        }

        if (warehouseRepository.existsByNameAndOrganization(dto.getName(), organization)) {
            throw new AlreadyExistException(WAREHOUSE_ALREADY_EXIST.getMessage());
        }


        Warehouse warehouse = Warehouse.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .organization(organization)
                .phone(dto.getPhone())
                .build();

        warehouseRepository.save(warehouse);
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    public WarehouseResponseDto getWarehouseById(Long id){
        Warehouse warehouse = findActiveWarehouseById(id);
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    public List<WarehouseResponseDto> getWarehouseByOrganizationId(Long organizationId){
        if (!organizationRepository.existsByIdAndStatus(organizationId, ItemStatus.ACTIVE)) {
            throw new NotFoundException(ORGANIZATION_NOT_FOUND.getMessage());
        }
        return warehouseMapper.toDtoList(
                warehouseRepository.findAllByOrganizationIdAndStatus(organizationId, ItemStatus.ACTIVE)
        );
    }

    @Override
    public List<WarehouseResponseDto> getAllWarehouse(){
            return warehouseMapper.toDtoList(warehouseRepository.findAllByStatus(ItemStatus.ACTIVE));
    }

    @Override
    @Transactional
    public WarehouseResponseDto updateWarehouse(Long id, UpdateWarehouseDto updatedWarehouse){
        Warehouse existingWarehouse = findActiveWarehouseById(id);
        warehouseMapper.updateFromDto(updatedWarehouse,existingWarehouse);
        return warehouseMapper.toDto(existingWarehouse);
    }

    @Override
    @Transactional
    public WarehouseResponseDto changeOrganization(Long warehouseId, ChangeWarehouseOrganizationDto dto){
        Warehouse warehouse = findActiveWarehouseById(warehouseId);

        Organization organization = organizationRepository.findByIdAndStatus(dto.getOrganizationId(),ItemStatus.ACTIVE)
                        .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));

        warehouse.setOrganization(organization);
        warehouseRepository.save(warehouse);
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    @Transactional
    public void deactiveWarehouse(Long id){
        Warehouse warehouse = warehouseRepository.findActiveByIdWithFullHierarchy(id)
                .orElseThrow(() -> new NotFoundException(WAREHOUSE_NOT_FOUND.getMessage()));

        warehouse.getZones().forEach(zone -> {
            zone.getShelves().forEach(shelf -> {
                shelf.getLocations().forEach(location -> {
                    location.setStatus(LocationStatus.INACTIVE);
                    location.setItemStatus(ItemStatus.INACTIVE);
                });
                shelf.setStatus(ItemStatus.INACTIVE);
            });
            zone.setStatus(ItemStatus.INACTIVE);
        });

        warehouse.setStatus(ItemStatus.INACTIVE);
        warehouseRepository.save(warehouse);
    }

    @Override
    public Warehouse getWarehouseEntityById(Long id){
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(WAREHOUSE_NOT_FOUND.getMessage()));
    }


    // HELPER METHODS!
    private Warehouse findActiveWarehouseById(Long id){
        return warehouseRepository.findByIdAndStatus(id, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(WAREHOUSE_NOT_FOUND.getMessage()));
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
