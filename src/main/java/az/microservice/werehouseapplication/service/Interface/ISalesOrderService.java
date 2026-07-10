package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.outbound.CreateSalesOrderDto;
import az.microservice.werehouseapplication.model.dto.request.outbound.CreateSalesOrderItemDto;
import az.microservice.werehouseapplication.model.dto.response.outbound.SalesOrderResponseDto;
import az.microservice.werehouseapplication.model.entity.outbount.SalesOrder;
import az.microservice.werehouseapplication.model.entity.partner.Partner;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.model.entity.transfer.TransferItem;

import java.util.List;

public interface ISalesOrderService {
    SalesOrderResponseDto create(CreateSalesOrderDto dto);
    SalesOrderResponseDto getById(Long id);
    List<SalesOrderResponseDto> getAll();
    List<SalesOrderResponseDto> getAllByOrganizationId(Long organizationId);
    void confirm(Long id);
    void cancel(Long id);
    void addItem(Long orderId, CreateSalesOrderItemDto dto);
    void removeItem(Long orderId, Long itemId);
    SalesOrder getSalesOrderEntityById(Long id);
    void updateStatus(Long id, az.microservice.werehouseapplication.enums.SalesOrderStatus status);
    SalesOrder createForTransfer(Transfer transfer, List<TransferItem> items,
                                 Partner customer, String username);
    void cancelByTransfer(Transfer transfer);
    void addItemForTransfer(Transfer transfer, TransferItem item);
    void updateItemQuantityForTransfer(Transfer transfer, TransferItem item, Integer newQuantity);
    void removeItemForTransfer(Transfer transfer, TransferItem item);

}
