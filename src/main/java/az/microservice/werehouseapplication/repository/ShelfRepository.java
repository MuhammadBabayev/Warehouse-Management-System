package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.warehouse.Shelf;
import az.microservice.werehouseapplication.model.entity.warehouse.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    List<Shelf> findAllByZoneId(Long zoneId);

    Optional<Shelf> findByIdAndStatus(Long id, ItemStatus itemStatus);

    List<Shelf> findAllByStatus(ItemStatus itemStatus);

    boolean existsByCodeAndZoneIdAndStatus(String code, Long zoneId, ItemStatus itemStatus);

    Optional<Shelf> findByCodeAndZoneAndStatus(String code, Zone zone, ItemStatus itemStatus);

    @Query("""
    SELECT s FROM Shelf s
    LEFT JOIN FETCH s.locations
    WHERE s.id = :id AND s.status = 'ACTIVE'
""")
    Optional<Shelf> findActiveByIdWithFullHierarchy(@Param("id") Long id);

//    List<Shelf> findAllByZone_Warehouse_OrganizationIdAndStatus(Long organizationId, ItemStatus status);
}
