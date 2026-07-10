package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.inbound.PurchaseOrder;
import az.microservice.werehouseapplication.model.entity.inbound.PurchaseOrderItem;
import az.microservice.werehouseapplication.model.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
    List<PurchaseOrderItem> findAllByPurchaseOrderId(Long purchaseOrderId);
    Optional<PurchaseOrderItem> findByIdAndPurchaseOrderId(Long id, Long purchaseOrderId);
    Optional<PurchaseOrderItem> findByPurchaseOrderAndProduct(PurchaseOrder purchaseOrder, Product product);
}
