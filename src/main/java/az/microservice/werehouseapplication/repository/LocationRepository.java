package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;
import az.microservice.werehouseapplication.model.entity.warehouse.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {


    Optional<Location> findByIdAndItemStatus(Long id, ItemStatus itemStatus);
    List<Location> findAllByItemStatus(ItemStatus itemStatus);
    List<Location> findAllByShelfIdAndItemStatus(Long shelfId, ItemStatus itemStatus);
//    List<Location> findAllByShelf_Zone_Warehouse_OrganizationIdAndItemStatus(Long organizationId, ItemStatus itemStatus);
    boolean existsByCodeAndShelf(String code, Shelf shelf);

    Optional<Location> findByIdAndStatus(Long fromLocationId, ItemStatus itemStatus);

    Optional<Location> findByCodeAndShelfAndItemStatus(String code, Shelf shelf, ItemStatus itemStatus);
}
