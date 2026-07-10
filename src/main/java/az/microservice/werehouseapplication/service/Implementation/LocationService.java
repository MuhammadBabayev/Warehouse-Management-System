package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.LocationStatus;
import az.microservice.werehouseapplication.exception.AlreadyExistException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.model.dto.request.location.CreateLocationDto;
import az.microservice.werehouseapplication.model.dto.request.location.UpdateLocationDto;
import az.microservice.werehouseapplication.model.dto.request.location.UpdateLocationStatusDto;
import az.microservice.werehouseapplication.model.dto.response.location.LocationResponseDto;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;
import az.microservice.werehouseapplication.model.entity.warehouse.Shelf;
import az.microservice.werehouseapplication.repository.LocationRepository;
import az.microservice.werehouseapplication.service.Interface.ILocationService;
import az.microservice.werehouseapplication.service.Interface.IShelfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.LOCATION_ALREADY_EXIST;
import static az.microservice.werehouseapplication.exception.ExceptionMessage.LOCATION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService implements ILocationService {

    private final LocationRepository locationRepository;
    private final IShelfService shelfService;

    @Override
    @Transactional
    public LocationResponseDto create(CreateLocationDto dto) {
        log.info("Creating location with code: {}", dto.getCode());
        Shelf shelf = shelfService.getShelfEntityById(dto.getShelfId());

        if (locationRepository.existsByCodeAndShelf(dto.getCode(), shelf)) {
            throw new AlreadyExistException(LOCATION_ALREADY_EXIST.getMessage());
        }

        Optional<Location> inactiveLocation = locationRepository
                .findByCodeAndShelfAndItemStatus(dto.getCode(), shelf, ItemStatus.INACTIVE);

        if(inactiveLocation.isPresent()){
            Location location = inactiveLocation.get();
            location.setItemStatus(ItemStatus.ACTIVE);
            location.setDescription(dto.getDescription());
            location.setMaxCapacity(dto.getMaxCapacity());
        }

        Location location = Location.builder()
                .code(dto.getCode())
                .description(dto.getDescription())
                .shelf(shelf)
                .maxCapacity(dto.getMaxCapacity())
                .build();

        return toResponse(locationRepository.save(location));
    }
    @Override
    public LocationResponseDto getById(Long id) {
        log.info("Fetching location with id: {}", id);
        return toResponse(findActiveLocationById(id));
    }

    @Override
    public List<LocationResponseDto> getAll() {
        log.info("Fetching all locations");

            return locationRepository.findAllByItemStatus(ItemStatus.ACTIVE)
                    .stream()
                    .map(this::toResponse)
                    .toList();

    }

    @Override
    public List<LocationResponseDto> getAllByShelfId(Long shelfId) {
        log.info("Fetching locations for shelf id: {}", shelfId);
        return locationRepository.findAllByShelfIdAndItemStatus(shelfId, ItemStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public LocationResponseDto update(Long id, UpdateLocationDto dto) {
        log.info("Updating location with id: {}", id);
        Location location = findActiveLocationById(id);

        if (dto.getShelfId() != null) {
            Shelf shelf = shelfService.getShelfEntityById(dto.getShelfId());
            location.setShelf(shelf);
        }
        if (dto.getCode() != null) {
            location.setCode(dto.getCode());
        }
        if (dto.getDescription() != null) {
            location.setDescription(dto.getDescription());
        }

        if (dto.getMaxCapacity() != null) {
            location.setMaxCapacity(dto.getMaxCapacity());
        }

        return toResponse(locationRepository.save(location));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, UpdateLocationStatusDto dto) {
        log.info("Updating status of location with id: {}", id);
        Location location = findActiveLocationById(id);
        location.setStatus(dto.getStatus());
        locationRepository.save(location);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting location with id: {}", id);
        Location location = findActiveLocationById(id);
        location.setStatus(LocationStatus.INACTIVE);
        location.setItemStatus(ItemStatus.INACTIVE);
        locationRepository.save(location);
    }

    @Override
    public Location getLocationEntityById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(LOCATION_NOT_FOUND.getMessage()));
    }

    // HELPER METHODS!
    private Location findActiveLocationById(Long id) {
        return locationRepository.findByIdAndItemStatus(id, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(LOCATION_NOT_FOUND.getMessage()));
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }


    private LocationResponseDto toResponse(Location location) {
        return LocationResponseDto.builder()
                .id(location.getId())
                .code(location.getCode())
                .description(location.getDescription())
                .status(location.getStatus())
                .maxCapacity(location.getMaxCapacity())
                .shelfId(location.getShelf().getId())
                .shelfCode(location.getShelf().getCode())
                .build();
    }
}
