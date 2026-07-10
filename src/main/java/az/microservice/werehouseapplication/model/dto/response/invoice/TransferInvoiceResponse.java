package az.microservice.werehouseapplication.model.dto.response.invoice;

import az.microservice.werehouseapplication.enums.InvoiceStatus;
import az.microservice.werehouseapplication.enums.InvoiceType;
import az.microservice.werehouseapplication.enums.TransferStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferInvoiceResponse {
    private Long id;
    private String checkNumber;
    private InvoiceType invoiceType;
    private InvoiceStatus status;

    private String transferNumber;        // e.g. TRF-2024-0001
    private TransferStatus transferStatus;

    private String organizationName;

    private String fromWarehouseName;
    private String fromLocationName;
    private String toWarehouseName;
    private String toLocationName;

    private String issuedByFullName;
    private LocalDateTime issuedAt;
    private LocalDateTime confirmedAt;

    private Integer totalItems;
    private Integer totalQuantity;
    private BigDecimal totalValue;

    private String notes;

    private List<TransferInvoiceItemResponse> items;
}
