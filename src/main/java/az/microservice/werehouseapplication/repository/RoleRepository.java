package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByIdAndStatus(Long id, ItemStatus status);
    boolean existsByName(String name);
    List<Role> findAllByStatus(ItemStatus status);

    Optional<Role> findByName(String roleName);
}
