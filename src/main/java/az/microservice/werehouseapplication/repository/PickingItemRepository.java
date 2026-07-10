package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.outbount.PickingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PickingItemRepository extends JpaRepository<PickingItem,Long> {
    List<PickingItem> findAllByPickingId(Long pickingId);
    Optional<PickingItem> findByIdAndPickingId(Long id, Long pickingId);

}
