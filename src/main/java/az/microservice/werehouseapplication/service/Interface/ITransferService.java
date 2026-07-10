package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.transfer.TransferItemRequestDto;
import az.microservice.werehouseapplication.model.dto.request.transfer.TransferRequestDto;
import az.microservice.werehouseapplication.model.dto.response.transfer.TransferResponseDto;

import java.util.List;

public interface ITransferService {

    TransferResponseDto create(TransferRequestDto dto);
    TransferResponseDto getById(Long id);
    List<TransferResponseDto> getAll();
    TransferResponseDto receive(Long id);           // PENDING → IN_PROGRESS
    TransferResponseDto ship(Long id);              // IN_PROGRESS → SHIPPED
//    CompleteTransferResponse complete(Long id);     // SHIPPED → COMPLETED
    void cancel(Long id);                           // PENDING → CANCELLED
    void addItem(Long transferId, TransferItemRequestDto dto);
    void updateItemQuantity(Long transferId, Long itemId, Integer newQuantity);
    void removeItem(Long transferId, Long itemId);
}
