package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.inbound.InboundReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InboundReceiptRepository extends JpaRepository<InboundReceipt, Long> {

    @Query("SELECT MAX(r.receiptNumber) FROM InboundReceipt r WHERE r.receiptNumber LIKE CONCAT('REC-', :year, '-%')")
    Optional<String> findMaxReceiptNumberByYear(@Param("year") String year);

    List<InboundReceipt> findAllByPurchaseOrder_OrganizationId(Long organizationId);

    List<InboundReceipt> findAllByPurchaseOrderId(Long purchaseOrderId);
}
