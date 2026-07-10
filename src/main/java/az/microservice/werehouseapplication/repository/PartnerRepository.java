package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.partner.Partner;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
    boolean existsByNameAndOrganization(String name, Organization organization);

    Optional<Partner> findByIdAndItemStatus(Long id, ItemStatus itemStatus);

    List<Partner> findAllByItemStatus(ItemStatus itemStatus);

    List<Partner> findAllByOrganizationIdAndItemStatus(Long id, ItemStatus itemStatus);
}
