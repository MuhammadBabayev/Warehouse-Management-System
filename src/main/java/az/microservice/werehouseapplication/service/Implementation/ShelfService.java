package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.LocationStatus;
import az.microservice.werehouseapplication.exception.AlreadyExistException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.mapper.ShelfMapper;
import az.microservice.werehouseapplication.model.dto.request.shelf.CreateShelfDto;
import az.microservice.werehouseapplication.model.dto.request.shelf.UpdateShelfDto;
import az.microservice.werehouseapplication.model.dto.response.shelf.ShelfResponseDto;
import az.microservice.werehouseapplication.model.entity.warehouse.Shelf;
import az.microservice.werehouseapplication.model.entity.warehouse.Zone;
import az.microservice.werehouseapplication.repository.ShelfRepository;
import az.microservice.werehouseapplication.service.Interface.IShelfService;
import az.microservice.werehouseapplication.service.Interface.IZoneService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.SHELF_ALREADY_EXIST;
import static az.microservice.werehouseapplication.exception.ExceptionMessage.SHELF_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShelfService implements IShelfService {
    private final ShelfRepository shelfRepository;
    private final IZoneService zoneService;
    private final ShelfMapper shelfMapper;

    @Transactional
    public ShelfResponseDto createShelf(CreateShelfDto request) {
        log.info("Creating shelf with code: {}", request.getCode());
        Zone zone = zoneService.getZoneEntityById(request.getZoneId());

        if (shelfRepository.existsByCodeAndZoneIdAndStatus(request.getCode(), request.getZoneId(), ItemStatus.ACTIVE)) {
            throw new AlreadyExistException(SHELF_ALREADY_EXIST.getMessage());
        }

        Optional<Shelf> inactiveShelf = shelfRepository
                .findByCodeAndZoneAndStatus(request.getCode(),zone,ItemStatus.INACTIVE);

        if(inactiveShelf.isPresent()){
            Shelf shelf = inactiveShelf.get();
            shelf.setStatus(ItemStatus.ACTIVE);
            shelf.setDescription(request.getDescription());
            return shelfMapper.toResponseDto(shelfRepository.save(shelf));
        }

        Shelf shelf = shelfMapper.toEntity(request);
        shelf.setZone(zone);
        Shelf saved = shelfRepository.save(shelf);

        return shelfMapper.toResponseDto(saved);
    }

    public ShelfResponseDto getShelfById(Long id) {
        log.info("Fetching shelf with id: {}", id);
        Shelf shelf = findActiveShelfById(id);
        return shelfMapper.toResponseDto(shelf);
    }

    public List<ShelfResponseDto> getAllShelf() {
            return shelfRepository.findAllByStatus(ItemStatus.ACTIVE)
                    .stream()
                    .map(shelfMapper::toResponseDto)
                    .collect(Collectors.toList());
    }

    public List<ShelfResponseDto> getAllByZoneId(Long zoneId) {
        log.info("Fetching shelves for zone id: {}", zoneId);
        return shelfRepository.findAllByZoneId(zoneId)
                .stream()
                .map(shelfMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public ShelfResponseDto updateShelf(Long id, UpdateShelfDto request) {
        log.info("Updating shelf with id: {}", id);
        Shelf shelf = findActiveShelfById(id);

        if (request.getZoneId() != null) {
            Zone zone = zoneService.getZoneEntityById(request.getZoneId());
            shelf.setZone(zone);
        }

        shelfMapper.updateEntityFromDto(request, shelf);
        Shelf updated = shelfRepository.save(shelf);
        return shelfMapper.toResponseDto(updated);
    }

    @Transactional
    public void deleteShelf(Long id) {
        log.info("Deleting shelf with id: {}", id);
        Shelf shelf = shelfRepository.findActiveByIdWithFullHierarchy(id)
                .orElseThrow(() -> new NotFoundException(SHELF_NOT_FOUND.getMessage()));

        shelf.getLocations().forEach(location -> {
            location.setStatus(LocationStatus.INACTIVE);
            location.setItemStatus(ItemStatus.INACTIVE);
        });

        shelf.setStatus(ItemStatus.INACTIVE);
        shelfRepository.save(shelf);
    }

    @Override
    public Shelf getShelfEntityById(Long id) {
        return shelfRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SHELF_NOT_FOUND.getMessage()));
    }

    // HELPER METHODS!
    private Shelf findActiveShelfById(Long id){
        return shelfRepository.findByIdAndStatus(id, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(SHELF_NOT_FOUND.getMessage()));
    }
}
