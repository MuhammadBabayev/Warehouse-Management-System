package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.outbound.CompletePickingItemDto;
import az.microservice.werehouseapplication.model.dto.request.outbound.CreatePickingDto;
import az.microservice.werehouseapplication.model.dto.response.outbound.PickingResponseDto;
import az.microservice.werehouseapplication.model.entity.outbount.Picking;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.model.entity.transfer.TransferItem;

import java.util.List;

public interface IPickingService {
    PickingResponseDto create(CreatePickingDto dto);
    PickingResponseDto getById(Long id);
    List<PickingResponseDto> getAllBySalesOrderId(Long salesOrderId);
    void start(Long id);
    void completeItem(Long pickingId, CompletePickingItemDto dto);
    void complete(Long id);
    Picking createForTransfer(Transfer transfer, List<TransferItem> items);
}
