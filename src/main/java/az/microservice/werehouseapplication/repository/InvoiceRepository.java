package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.InvoiceType;
import az.microservice.werehouseapplication.model.entity.finance.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsByTransferIdAndInvoiceType(Long transferId, InvoiceType invoiceType);

//    Optional<Invoice> findByCheckNumber(String checkNumber);

    List<Invoice> findAllByOrganizationIdOrderByIssuedAtDesc(Long organizationId);

    List<Invoice> findAllByTransferId(Long transferId);
}
