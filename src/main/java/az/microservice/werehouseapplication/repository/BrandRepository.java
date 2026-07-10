package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.product.Brand;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

//    boolean existsByName(String name);

    Optional<Brand> findByIdAndStatus(Long id, ItemStatus status);
    List<Brand> findAllByStatus(ItemStatus status);
    List<Brand> findAllByOrganizationIdAndStatus(Long organizationId, ItemStatus status);
    boolean existsByNameAndOrganization(String name, Organization organization);
    boolean existsByName(String name);
    boolean existsByOrganization(Organization organization);
    Optional<Brand> findByNameAndOrganizationAndStatus(String name, Organization organization, ItemStatus itemStatus);
}
