package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.outbount.SalesOrder;
import az.microservice.werehouseapplication.model.entity.outbount.SalesOrderItem;
import az.microservice.werehouseapplication.model.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem,Long> {
    List<SalesOrderItem> findAllBySalesOrderId(Long salesOrderId);
    Optional<SalesOrderItem> findByIdAndSalesOrderId(Long id, Long salesOrderId);
    Optional<SalesOrderItem> findBySalesOrderAndProduct(SalesOrder salesOrder, Product product);
}
