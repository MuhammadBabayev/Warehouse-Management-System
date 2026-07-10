package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.InboundReceiptStatus;
import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.PurchaseOrderStatus;
import az.microservice.werehouseapplication.exception.InternalServerError;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.exception.old.MyException;
import az.microservice.werehouseapplication.model.dto.request.inboundReceipt.CreateInboundReceiptDto;
import az.microservice.werehouseapplication.model.dto.response.inboundReceipt.InboundReceiptItemResponseDto;
import az.microservice.werehouseapplication.model.dto.response.inboundReceipt.InboundReceiptResponseDto;
import az.microservice.werehouseapplication.model.entity.inbound.InboundReceipt;
import az.microservice.werehouseapplication.model.entity.inbound.InboundReceiptItem;
import az.microservice.werehouseapplication.model.entity.inbound.PurchaseOrder;
import az.microservice.werehouseapplication.model.entity.inbound.PurchaseOrderItem;
import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.model.entity.transfer.TransferItem;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;
import az.microservice.werehouseapplication.repository.*;
import az.microservice.werehouseapplication.service.Interface.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InboundReceiptService implements IInboundReceiptService {
    private final InboundReceiptRepository inboundReceiptRepository;
    private final InboundReceiptItemRepository inboundReceiptItemRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final IPurchaseOrderService purchaseOrderService;
    private final IProductService productService;
    private final ILocationService locationService;
    private final IInventoryService inventoryService;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public InboundReceiptResponseDto create(CreateInboundReceiptDto dto) {
        log.info("Creating inbound receipt for purchase order id: {}", dto.getPurchaseOrderId());

        User user = userRepository.findByIdAndItemStatus(dto.getUserId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        PurchaseOrder purchaseOrder = purchaseOrderService.getPurchaseOrderEntityById(dto.getPurchaseOrderId());

        if (purchaseOrder.getStatus() != PurchaseOrderStatus.CONFIRMED &&
                purchaseOrder.getStatus() != PurchaseOrderStatus.PARTIALLY_RECEIVED) {
            throw new InternalServerError(INTERNAL_SERVER_ERROR.getMessage());
        }

        String year = String.valueOf(LocalDateTime.now().getYear());

        String maxReceiptNumber = inboundReceiptRepository.findMaxReceiptNumberByYear(year)
                .orElse("REC-" + year + "-0000");

        int lastSequence = Integer.parseInt(maxReceiptNumber.split("-")[2]);
        String sequence = String.format("%04d", lastSequence + 1);
        String receiptNumber = "REC-" + year + "-" + sequence;

        InboundReceipt receipt = InboundReceipt.builder()
                .purchaseOrder(purchaseOrder)
                .receivedBy(user)
                .receiptNumber(receiptNumber)
                .notes(dto.getNotes())
                .build();

        inboundReceiptRepository.save(receipt);

        List<InboundReceiptItem> items = dto.getItems().stream()
                .map(itemDto -> {
                    Product product = productService.getProductEntityById(itemDto.getProductId());
                    Location location = locationService.getLocationEntityById(itemDto.getLocationId());

                    return InboundReceiptItem.builder()
                            .inboundReceipt(receipt)
                            .product(product)
                            .location(location)
                            .expectedQuantity(itemDto.getExpectedQuantity())
                            .receivedQuantity(itemDto.getReceivedQuantity())
                            .rejectedQuantity(itemDto.getRejectedQuantity() != null ? itemDto.getRejectedQuantity() : 0)
                            .notes(itemDto.getNotes())
                            .build();
                })
                .toList();

        inboundReceiptItemRepository.saveAll(items);

        return buildResponse(receipt, items);

    }

    @Override
    public InboundReceiptResponseDto getById(Long inbound_receipt_id) {
        log.info("Fetching inbound receipt with id: {}", inbound_receipt_id);

        InboundReceipt receipt = inboundReceiptRepository.findById(inbound_receipt_id)
                .orElseThrow(() -> new NotFoundException(INBOUND_RECEIPT_NOT_FOUND.getMessage()));

        List<InboundReceiptItem> items = inboundReceiptItemRepository.findAllByInboundReceiptId(inbound_receipt_id);

        return buildResponse(receipt, items);
    }

    @Override
    public List<InboundReceiptResponseDto> getAll() {
        log.info("Fetching all inbound receipts");


        List<InboundReceipt> receipts = inboundReceiptRepository.findAll();

        return receipts.stream()
                .map(receipt -> buildResponse(receipt,
                        inboundReceiptItemRepository.findAllByInboundReceiptId(receipt.getId())))
                .toList();
    }


    @Override
    public List<InboundReceiptResponseDto> getAllByPurchaseOrderId(Long purchaseOrderId) {
        log.info("Fetching inbound receipts for purchase order id: {}", purchaseOrderId);

        List<InboundReceipt> receipts = inboundReceiptRepository.findAllByPurchaseOrderId(purchaseOrderId);

        return receipts.stream()
                .map(receipt -> buildResponse(receipt,
                        inboundReceiptItemRepository.findAllByInboundReceiptId(receipt.getId())))
                .toList();
    }

    @Override
    @Transactional
    public void complete(Long id) {
        log.info("Completing inbound receipt with id: {}", id);

        InboundReceipt receipt = inboundReceiptRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(INBOUND_RECEIPT_NOT_FOUND.getMessage()));

        if (receipt.getStatus() != InboundReceiptStatus.PENDING) {
            throw new InternalServerError(INTERNAL_SERVER_ERROR.getMessage());
        }

        receipt.setStatus(InboundReceiptStatus.COMPLETED);
        inboundReceiptRepository.save(receipt);

        List<InboundReceiptItem> receiptItems = inboundReceiptItemRepository.findAllByInboundReceiptId(id);


        receiptItems.forEach(item -> {
            inventoryService.increaseStock(
                    item.getProduct().getId(),
                    item.getLocation().getId(),
                    item.getReceivedQuantity()
            );
//            if (item.getRejectedQuantity() > 0) {
//                inventoryService.increaseDamagedStock(
//                        item.getProduct().getId(),
//                        item.getLocation().getId(),
//                        item.getRejectedQuantity(),
//                        ReferenceType.PURCHASE_ORDER,
//                        receipt.getPurchaseOrder().getId()
//                );
//            }
        });


        List<PurchaseOrderItem> allOrderItems = purchaseOrderItemRepository
                .findAllByPurchaseOrderId(receipt.getPurchaseOrder().getId());

        int totalOrdered = allOrderItems.stream().mapToInt(PurchaseOrderItem::getQuantity).sum();
        int totalReceived = receiptItems.stream().mapToInt(InboundReceiptItem::getReceivedQuantity).sum();

        if (totalReceived >= totalOrdered) {
            purchaseOrderService.updateStatusFromInbound(
                    receipt.getPurchaseOrder().getId(), PurchaseOrderStatus.RECEIVED);
        } else {
            purchaseOrderService.updateStatusFromInbound(
                    receipt.getPurchaseOrder().getId(), PurchaseOrderStatus.PARTIALLY_RECEIVED);
        }
    }

    @Override
    @Transactional
    public void reject(Long inbound_receipt_id) {
        log.info("Rejecting inbound receipt with id: {}", inbound_receipt_id);

        InboundReceipt receipt = inboundReceiptRepository.findById(inbound_receipt_id)
                .orElseThrow(() -> new MyException("Inbound receipt not found with id: " + inbound_receipt_id));

        if (receipt.getStatus() != InboundReceiptStatus.PENDING) {
            throw new InternalServerError(INTERNAL_SERVER_ERROR.getMessage());
        }

        receipt.setStatus(InboundReceiptStatus.REJECTED);
        inboundReceiptRepository.save(receipt);
    }


    @Transactional
    public InboundReceipt createForTransfer(Transfer transfer, List<TransferItem> items) {
        log.info("Creating InboundReceipt for transfer: {}", transfer.getTransferNumber());

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByTransfer(transfer)
                .orElseThrow(() -> new MyException("PurchaseOrder not found for transfer: " + transfer.getId()));

        User receivedBy = userRepository.findByUsername(transfer.getCreatedBy())
                .orElseThrow(() -> new MyException("User not found: " + transfer.getCreatedBy()));

        Long nextVal = (Long) entityManager
                .createNativeQuery("SELECT NEXTVAL('receipt_number_seq')")
                .getSingleResult();
        String receiptNumber = "REC-" + LocalDateTime.now().getYear() + "-" + String.format("%04d", nextVal);

        InboundReceipt receipt = InboundReceipt.builder()
                .purchaseOrder(purchaseOrder)
                .receivedBy(receivedBy)
                .receiptNumber(receiptNumber)
                .status(InboundReceiptStatus.PENDING)
                .notes("Auto-created for transfer: " + transfer.getTransferNumber())
                .build();

        inboundReceiptRepository.save(receipt);

        List<InboundReceiptItem> receiptItems = items.stream()
                .map(item -> {
                    inventoryService.increaseStock(
                            item.getProduct().getId(),
                            item.getToLocation().getId(),
                            item.getQuantity()
                    );

                    return InboundReceiptItem.builder()
                            .inboundReceipt(receipt)
                            .product(item.getProduct())
                            .location(item.getToLocation())
                            .expectedQuantity(item.getQuantity())
                            .receivedQuantity(item.getQuantity())
                            .rejectedQuantity(0)
                            .build();
                }).toList();

        inboundReceiptItemRepository.saveAll(receiptItems);

        receipt.setStatus(InboundReceiptStatus.COMPLETED);
        inboundReceiptRepository.save(receipt);

        purchaseOrder.setStatus(PurchaseOrderStatus.RECEIVED);
        purchaseOrderRepository.save(purchaseOrder);

        log.info("InboundReceipt completed for transfer: {}", transfer.getTransferNumber());
        return receipt;
    }



    private InboundReceiptResponseDto buildResponse(InboundReceipt receipt, List<InboundReceiptItem> items) {
        List<InboundReceiptItemResponseDto> itemDtos = items.stream()
                .map(item -> InboundReceiptItemResponseDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .locationId(item.getLocation().getId())
                        .locationCode(item.getLocation().getCode())
                        .expectedQuantity(item.getExpectedQuantity())
                        .receivedQuantity(item.getReceivedQuantity())
                        .rejectedQuantity(item.getRejectedQuantity())
                        .notes(item.getNotes())
                        .build())
                .toList();

        return InboundReceiptResponseDto.builder()
                .id(receipt.getId())
                .receiptNumber(receipt.getReceiptNumber())
                .status(receipt.getStatus())
                .purchaseOrderId(receipt.getPurchaseOrder().getId())
                .purchaseOrderNumber(receipt.getPurchaseOrder().getOrderNumber())
                .receivedByUsername(receipt.getReceivedBy().getUsername())
                .receivedAt(receipt.getReceivedAt())
                .createdAt(receipt.getCreatedAt())
                .notes(receipt.getNotes())
                .items(itemDtos)
                .build();
    }

    // endregion
}
