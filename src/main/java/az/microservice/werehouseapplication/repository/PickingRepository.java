package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.outbount.Picking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PickingRepository extends JpaRepository<Picking,Long> {
    List<Picking> findAllBySalesOrderId(Long salesOrderId);
}
