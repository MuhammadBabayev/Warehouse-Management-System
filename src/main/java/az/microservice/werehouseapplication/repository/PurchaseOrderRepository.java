package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.inbound.PurchaseOrder;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @Query("SELECT MAX(p.orderNumber) FROM PurchaseOrder p WHERE p.orderNumber LIKE CONCAT('PO-', :year, '-%')")
    Optional<String> findMaxOrderNumberByYear(@Param("year") String year);

    List<PurchaseOrder> findAllByOrganizationId(Long organizationId);

    Optional<PurchaseOrder> findByTransfer(Transfer transfer);
}
