package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.users.Role;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.users.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT ur FROM UserRole ur JOIN FETCH ur.role WHERE ur.user = :user")
    List<UserRole> findByUser(@Param("user") User user);

//    List<UserRole> findByUserId(Long userId);
    boolean existsByUserAndRole(User user, Role role);

    boolean existsByUserAndRole_Name(User user, String roleName);
}
