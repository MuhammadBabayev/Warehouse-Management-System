package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.enums.PurchaseOrderStatus;
import az.microservice.werehouseapplication.model.dto.request.purchaseOrder.CreatePurchaseOrderDto;
import az.microservice.werehouseapplication.model.dto.request.purchaseOrder.CreatePurchaseOrderItemDto;
import az.microservice.werehouseapplication.model.dto.response.purchaseOrder.PurchaseOrderResponseDto;
import az.microservice.werehouseapplication.model.entity.inbound.PurchaseOrder;
import az.microservice.werehouseapplication.model.entity.partner.Partner;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.model.entity.transfer.TransferItem;

import java.util.List;

public interface IPurchaseOrderService {

    PurchaseOrderResponseDto create(CreatePurchaseOrderDto dto);
    PurchaseOrderResponseDto getById(Long id);
    List<PurchaseOrderResponseDto> getAll();
    List<PurchaseOrderResponseDto> getAllByOrganizationId(Long id);
    void addItem(Long orderId, CreatePurchaseOrderItemDto dto);
    void removeItem(Long orderId, Long itemId);
    void confirmPurchaseOrderStatusFromDraft(Long id);
    void cancelPurchaseOrderStatus(Long id);
    PurchaseOrder createForTransfer(Transfer transfer, List<TransferItem> items,
                                    Partner vendor, String username);
    void cancelByTransfer(Transfer transfer);
    void addItemForTransfer(Transfer transfer, TransferItem item);
    void updateItemQuantityForTransfer(Transfer transfer, TransferItem item, Integer newQuantity);
    void removeItemForTransfer(Transfer transfer, TransferItem item);

    //Bunlar endpoint kimi yazilmir komekci metodlardir!!

    PurchaseOrder getPurchaseOrderEntityById(Long id);

    void updateStatusFromInbound(Long id, PurchaseOrderStatus status);



}
