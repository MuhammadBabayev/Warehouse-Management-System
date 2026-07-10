package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
//    Optional<Organization> findByName(String name);
    boolean existsByName(String name);

    Optional<Organization> findByIdAndStatus(Long id, ItemStatus itemStatus);

    boolean existsByIdAndStatus(Long organizationId, ItemStatus itemStatus);

    Optional<Organization> findByEmailAndStatus(String email, ItemStatus itemStatus);
}
