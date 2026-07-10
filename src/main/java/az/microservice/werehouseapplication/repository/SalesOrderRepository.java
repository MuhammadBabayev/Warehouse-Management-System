package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.outbount.SalesOrder;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalesOrderRepository extends JpaRepository<SalesOrder,Long> {
    List<SalesOrder> findAllByOrganizationId(Long organizationId);

    Optional<SalesOrder> findByTransfer(Transfer transfer);

    @Query("SELECT MAX(s.orderNumber) FROM SalesOrder s WHERE s.orderNumber LIKE CONCAT('SO-', :year, '-%')")
    Optional<String> findMaxOrderNumberByYear(@Param("year") String year);
}
