package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.LocationStatus;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.exception.old.MyException;
import az.microservice.werehouseapplication.mapper.ZoneMapper;
import az.microservice.werehouseapplication.model.dto.request.zone.CreateZoneDto;
import az.microservice.werehouseapplication.model.dto.request.zone.UpdateZoneDto;
import az.microservice.werehouseapplication.model.dto.response.zone.ZoneResponseDto;
import az.microservice.werehouseapplication.model.entity.warehouse.Warehouse;
import az.microservice.werehouseapplication.model.entity.warehouse.Zone;
import az.microservice.werehouseapplication.repository.ZoneRepository;
import az.microservice.werehouseapplication.service.Interface.IWarehouseService;
import az.microservice.werehouseapplication.service.Interface.IZoneService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.ZONE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZoneService implements IZoneService {
    private final ZoneRepository zoneRepository;
    private final IWarehouseService warehouseService;
    private final ZoneMapper zoneMapper;

    @Override
    @Transactional
    public ZoneResponseDto createZone(CreateZoneDto dto) {
        log.info("Creating zone with name: {}", dto.getName());
        Warehouse warehouse = warehouseService.getWarehouseEntityById(dto.getWarehouseId());

        Optional<Zone> inactiveZone = zoneRepository
                .findByNameAndStatus(dto.getName(), ItemStatus.INACTIVE);

        if(inactiveZone.isPresent()){
            Zone zone = inactiveZone.get();
            zone.setStatus(ItemStatus.ACTIVE);
            zone.setDescription(dto.getDescription());
            return zoneMapper.toResponseDto(zoneRepository.save(zone));
        }

        Zone zone = zoneMapper.toEntity(dto);
        zone.setWarehouse(warehouse);
        Zone saved = zoneRepository.save(zone);

        return zoneMapper.toResponseDto(saved);
    }

    @Override
    public ZoneResponseDto getZoneById(Long id) {
        log.info("Fetching zone with id: {}", id);
        Zone zone = findActiveZoneById(id);
        return zoneMapper.toResponseDto(zone);
    }

    @Override
    public List<ZoneResponseDto> getAllZone() {
            return zoneRepository.findAllByStatus(ItemStatus.ACTIVE)
                    .stream()
                    .map(zoneMapper::toResponseDto)
                    .toList();

    }

    @Override
    public List<ZoneResponseDto> getAllByWarehouseId(Long warehouseId) {
        log.info("Fetching zones for warehouse id: {}", warehouseId);
        return zoneRepository.findAllByWarehouseId(warehouseId)
                .stream()
                .map(zoneMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ZoneResponseDto updateZone(Long id, UpdateZoneDto dto) {
        log.info("Updating zone with id: {}", id);
        Zone zone = findActiveZoneById(id);
        zoneMapper.updateEntityFromDto(dto, zone);
        Zone updated = zoneRepository.save(zone);
        return zoneMapper.toResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteZone(Long id) {
        log.info("Deleting zone with id: {}", id);
        Zone zone = zoneRepository.findActiveByIdWithFullHierarchy(id)
                        .orElseThrow(() -> new NotFoundException(ZONE_NOT_FOUND.getMessage()));

        zone.getShelves().forEach(shelf -> {
            shelf.getLocations().forEach(location -> {
                location.setStatus(LocationStatus.INACTIVE);
                location.setItemStatus(ItemStatus.INACTIVE);
            });
            shelf.setStatus(ItemStatus.INACTIVE);
        });

        zone.setStatus(ItemStatus.INACTIVE);
        zoneRepository.save(zone);
    }

    @Override
    public Zone getZoneEntityById(Long id){
        return zoneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ZONE_NOT_FOUND.getMessage()));
    }

    // HELPER METHODS!
    private Zone findActiveZoneById(Long id){
        return zoneRepository.findByIdAndStatus(id,ItemStatus.ACTIVE)
                .orElseThrow(() -> new MyException("Zone not found with id: " + id));
    }
}
