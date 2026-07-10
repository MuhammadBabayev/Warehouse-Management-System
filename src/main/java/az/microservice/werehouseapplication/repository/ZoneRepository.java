package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.warehouse.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Optional<Zone> findByNameAndStatus(String name, ItemStatus itemStatus);
    List<Zone> findAllByWarehouseId(Long warehouseId);
    List<Zone> findAllByStatus(ItemStatus itemStatus);
    Optional<Zone> findByIdAndStatus(Long id, ItemStatus itemStatus);

    @Query("""
    SELECT z FROM Zone z
    LEFT JOIN FETCH z.shelves s
    LEFT JOIN FETCH s.locations
    WHERE z.id = :id AND z.status = 'ACTIVE'
""")
    Optional<Zone> findActiveByIdWithFullHierarchy(@Param("id") Long id);

//    List<Zone> findAllByWarehouse_OrganizationIdAndStatus(Long organizationId, ItemStatus status);
}
