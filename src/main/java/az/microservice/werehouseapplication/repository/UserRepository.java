package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.UserStatus;
import az.microservice.werehouseapplication.model.entity.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findAllByItemStatus(ItemStatus itemStatus);
    List<User> findAllByOrganizationIdAndItemStatus(Long organizationId, ItemStatus itemStatus);
    Optional<User> findByIdAndStatus(Long userId, UserStatus status);

    Optional<User> findByIdAndItemStatus(Long userId, ItemStatus itemStatus);
}
