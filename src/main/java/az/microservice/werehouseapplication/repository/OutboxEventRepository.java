package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.OutboxStatus;
import az.microservice.werehouseapplication.model.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatusAndRetryCountLessThan(OutboxStatus status, int maxRetry);
}