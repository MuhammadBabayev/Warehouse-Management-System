package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    
//    List<Transfer> findAllByOrganizationId(Long organizationId);

}
