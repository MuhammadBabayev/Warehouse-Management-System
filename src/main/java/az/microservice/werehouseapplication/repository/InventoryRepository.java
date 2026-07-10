package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.transfer.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProduct_IdAndLocation_Id(Long productId, Long locationId);
    List<Inventory> findAllByProduct_Id(Long productId);
    List<Inventory> findAllByLocation_Id(Long locationId);
    List<Inventory> findAllByLocation_Shelf_Zone_WarehouseId(Long warehouseId);
    List<Inventory> findAllByLocation_Shelf_Zone_Warehouse_OrganizationId(Long organizationId);

    @Query("SELECT i FROM Inventory i WHERE i.id = :id")
    Optional<Inventory> findInventoryById(@Param("id") Long id);
}
