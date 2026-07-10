package az.microservice.werehouseapplication.model.dto.response.transfer;

import az.microservice.werehouseapplication.model.dto.response.invoice.TransferInvoiceResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CompleteTransferResponse {
    private TransferInvoiceResponse outgoingInvoice;
    private TransferInvoiceResponse incomingInvoice;
}
