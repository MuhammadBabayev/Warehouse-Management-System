package az.microservice.werehouseapplication.model.dto.request.invoice;

import az.microservice.werehouseapplication.enums.InvoiceType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateInvoiceRequest {
    @NotNull
    private Long transferId;

    @NotNull
    private InvoiceType invoiceType;

    private String notes;
}
