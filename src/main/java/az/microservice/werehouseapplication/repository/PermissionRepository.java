package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.users.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permissions,Long> {
    Optional<Permissions> findByName(String name);
    boolean existsByName(String name);
    List<Permissions> findAllByStatus(ItemStatus status);
    Optional<Permissions> findByIdAndStatus(Long id, ItemStatus itemStatus);
}
