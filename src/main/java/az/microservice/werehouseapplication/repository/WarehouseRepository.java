package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import az.microservice.werehouseapplication.model.entity.warehouse.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository <Warehouse, Long> {
//    List<Warehouse> findAllByOrganizationId(Long organizationId);

    Optional<Warehouse> findByIdAndStatus(Long id, ItemStatus itemStatus);
    List<Warehouse> findAllByStatus(ItemStatus itemStatus);
    List<Warehouse> findAllByOrganizationIdAndStatus(Long organizationId, ItemStatus status);
    Boolean existsByNameAndOrganization(String name, Organization organization);

    @Query("""
    SELECT w FROM Warehouse w
    LEFT JOIN FETCH w.zones z
    LEFT JOIN FETCH z.shelves s
    LEFT JOIN FETCH s.locations
    WHERE w.id = :id AND w.status = 'ACTIVE'
""")
    Optional<Warehouse> findActiveByIdWithFullHierarchy(@Param("id") Long id);

    Optional<Warehouse> findByNameAndOrganizationAndStatus(String name, Organization organization, ItemStatus itemStatus);
}
