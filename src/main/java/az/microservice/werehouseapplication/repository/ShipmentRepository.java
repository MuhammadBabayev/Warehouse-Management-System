package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.outbount.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment,Long> {
    List<Shipment> findAllBySalesOrderId(Long salesOrderId);
}
