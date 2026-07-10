package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.inboundReceipt.CreateInboundReceiptDto;
import az.microservice.werehouseapplication.model.dto.response.inboundReceipt.InboundReceiptResponseDto;
import az.microservice.werehouseapplication.model.entity.inbound.InboundReceipt;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.model.entity.transfer.TransferItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IInboundReceiptService {

    InboundReceiptResponseDto create(CreateInboundReceiptDto dto);
    InboundReceiptResponseDto getById(Long inbound_receipt_id);
    List<InboundReceiptResponseDto> getAll();
    List<InboundReceiptResponseDto> getAllByPurchaseOrderId(Long purchaseOrderId);
    void complete(Long inbound_receipt_id);
    void reject(Long inbound_receipt_id);
    InboundReceipt createForTransfer(Transfer transfer, List<TransferItem> items);
}
