package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.transfer.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    @Query("SELECT sm FROM StockMovement sm WHERE sm.product.id = :productId ORDER BY sm.createdAt DESC")
    List<StockMovement> findByProductId(@Param("productId") Long productId);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.referenceType = :referenceType AND sm.referenceId = :referenceId ORDER BY sm.createdAt DESC")
    List<StockMovement> findByReferenceTypeAndReferenceId(@Param("referenceType") String referenceType, @Param("referenceId") Long referenceId);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.createdAt BETWEEN :from AND :to ORDER BY sm.createdAt DESC")
    List<StockMovement> findByCreatedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT sm FROM StockMovement sm " +
            "JOIN sm.location l " +
            "JOIN l.shelf s " +
            "JOIN s.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId " +
            "ORDER BY sm.createdAt DESC")
    List<StockMovement> findByWarehouseId(@Param("warehouseId") Long warehouseId);



//    List<StockMovement> findAllByProductId(Long productId);
    List<StockMovement> findAllByLocationId(Long locationId);
    List<StockMovement> findAllByLocation_Shelf_Zone_Warehouse_OrganizationId(Long organizationId);
}
