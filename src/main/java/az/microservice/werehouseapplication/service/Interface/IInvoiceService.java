package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.enums.InvoiceType;
import az.microservice.werehouseapplication.model.dto.request.invoice.CheckActionRequest;
import az.microservice.werehouseapplication.model.dto.response.invoice.TransferInvoiceResponse;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;

import java.util.List;

public interface IInvoiceService {
    TransferInvoiceResponse generateCheck(Transfer transfer, InvoiceType invoiceType, String username);

    TransferInvoiceResponse getById(Long checkId);

    List<TransferInvoiceResponse> getByTransfer(Long transferId);

    List<TransferInvoiceResponse> getAllByOrganization(Long organizationId);

    TransferInvoiceResponse issueInvoice(Long invoiceId, CheckActionRequest request);

    TransferInvoiceResponse confirmCheck(Long checkId, CheckActionRequest request);

    TransferInvoiceResponse disputeCheck(Long checkId, CheckActionRequest request);

}
