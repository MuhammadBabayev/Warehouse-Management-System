package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.inbound.InboundReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InboundReceiptItemRepository extends JpaRepository<InboundReceiptItem, Long> {

    List<InboundReceiptItem> findAllByInboundReceiptId(Long inboundReceiptId);
}
