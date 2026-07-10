package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.transfer.TransferItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferItemRepository extends JpaRepository <TransferItem, Long> {

    List<TransferItem> findAllByTransferId(Long transferId);
    Optional<TransferItem> findByIdAndTransferId(Long id, Long transferId);
}
