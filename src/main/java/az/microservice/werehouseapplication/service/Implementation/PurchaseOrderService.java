package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.PurchaseOrderStatus;
import az.microservice.werehouseapplication.exception.InvalidOrderStatusException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.exception.PurchaseOrderCreationNotAllowedException;
import az.microservice.werehouseapplication.exception.old.MyException;
import az.microservice.werehouseapplication.mapper.PurchaseOrderMapper;
import az.microservice.werehouseapplication.model.dto.request.purchaseOrder.CreatePurchaseOrderDto;
import az.microservice.werehouseapplication.model.dto.request.purchaseOrder.CreatePurchaseOrderItemDto;
import az.microservice.werehouseapplication.model.dto.response.purchaseOrder.PurchaseOrderResponseDto;
import az.microservice.werehouseapplication.model.entity.inbound.PurchaseOrder;
import az.microservice.werehouseapplication.model.entity.inbound.PurchaseOrderItem;
import az.microservice.werehouseapplication.model.entity.partner.Partner;
import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.model.entity.transfer.TransferItem;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import az.microservice.werehouseapplication.model.entity.warehouse.Warehouse;
import az.microservice.werehouseapplication.repository.PurchaseOrderItemRepository;
import az.microservice.werehouseapplication.repository.PurchaseOrderRepository;
import az.microservice.werehouseapplication.repository.UserRepository;
import az.microservice.werehouseapplication.service.Interface.IPartnerService;
import az.microservice.werehouseapplication.service.Interface.IProductService;
import az.microservice.werehouseapplication.service.Interface.IPurchaseOrderService;
import az.microservice.werehouseapplication.service.Interface.IWarehouseService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderService implements IPurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final IPartnerService vendorService;
    private final IWarehouseService warehouseService;
    private final IProductService productService;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final UserRepository userRepository;
    private final EntityManager entityManager;


    @Override
    @Transactional
    public PurchaseOrderResponseDto create(CreatePurchaseOrderDto dto){
        log.info("Creating purchase order");


        User user = userRepository.findByIdAndItemStatus(dto.getUserId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        Organization organization = user.getOrganization();

        if (organization == null) {
            throw new PurchaseOrderCreationNotAllowedException(SUPER_ADMIN_CANNOT_CREATE_PURCHASE_ORDER.getMessage());
        }

        Partner vendor = vendorService.getPartnerEntityById(dto.getPartnerId());
        Warehouse warehouse = warehouseService.getWarehouseEntityById(dto.getWarehouseId());

        String year = String.valueOf(LocalDateTime.now().getYear());

        String maxOrderNumber = purchaseOrderRepository.findMaxOrderNumberByYear(year)
                .orElse("PO-" + year + "-0000");

        int lastSequence = Integer.parseInt(maxOrderNumber.split("-")[2]);
        String sequence = String.format("%04d", lastSequence + 1);
        String orderNumber = "PO-" + year + "-" + sequence;

        PurchaseOrder order = PurchaseOrder.builder()
                .organization(organization)
                .vendor(vendor)
                .warehouse(warehouse)
                .createdBy(user)
                .orderNumber(orderNumber)
                .expectedAt(dto.getExpectedAt())
                .notes(dto.getNotes())
                .build();

        purchaseOrderRepository.save(order);

        List<PurchaseOrderItem> items = dto.getItems().stream()
                .map(itemDto -> {
                    Product product = productService.getProductEntityById(itemDto.getProductId());
                    return PurchaseOrderItem.builder()
                            .purchaseOrder(order)
                            .product(product)
                            .quantity(itemDto.getQuantity())
                            .unitPrice(itemDto.getUnitPrice())
                            .build();
                })
                .toList();

        purchaseOrderItemRepository.saveAll(items);

        PurchaseOrderResponseDto response = purchaseOrderMapper.toResponseDto(order);
        response.setItems(items.stream().map(purchaseOrderMapper::toItemResponseDto).toList());
        return response;
    }


    @Override
    public PurchaseOrderResponseDto getById(Long id) {
        log.info("Fetching purchase order with id: {}", id);

        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PURCHASE_ORDER_NOT_FOUND.getMessage()));

        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findAllByPurchaseOrderId(id);

        PurchaseOrderResponseDto response = purchaseOrderMapper.toResponseDto(order);
        response.setItems(items.stream().map(purchaseOrderMapper::toItemResponseDto).toList());
        return response;
    }


    @Override
    public List<PurchaseOrderResponseDto> getAll() {
        log.info("Fetching all purchase orders");

        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();

        return orders.stream()
                .map(order -> {
                    List<PurchaseOrderItem> items = purchaseOrderItemRepository.findAllByPurchaseOrderId(order.getId());
                    PurchaseOrderResponseDto response = purchaseOrderMapper.toResponseDto(order);
                    response.setItems(items.stream().map(purchaseOrderMapper::toItemResponseDto).toList());
                    return response;
                })
                .toList();
    }

    @Override
    public List<PurchaseOrderResponseDto> getAllByOrganizationId(Long id) {
        log.info("Fetching all purchase orders");

        List<PurchaseOrder> orders = purchaseOrderRepository.findAllByOrganizationId(id);

        return orders.stream()
                .map(order -> {
                    List<PurchaseOrderItem> items = purchaseOrderItemRepository.findAllByPurchaseOrderId(order.getId());
                    PurchaseOrderResponseDto response = purchaseOrderMapper.toResponseDto(order);
                    response.setItems(items.stream().map(purchaseOrderMapper::toItemResponseDto).toList());
                    return response;
                })
                .toList();
    }


    @Override
    @Transactional
    public void addItem(Long orderId, CreatePurchaseOrderItemDto dto) {
        log.info("Adding item to purchase order with id: {}", orderId);

        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(PURCHASE_ORDER_NOT_FOUND.getMessage()));

        if (order.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new InvalidOrderStatusException(ITEMS_CAN_ONLY_BE_ADDED_TO_DRAFT_ORDER.getMessage());
        }

        Product product = productService.getProductEntityById(dto.getProductId());

        PurchaseOrderItem item = PurchaseOrderItem.builder()
                .purchaseOrder(order)
                .product(product)
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .build();

        purchaseOrderItemRepository.save(item);
    }


    @Override
    @Transactional
    public void removeItem(Long orderId, Long itemId) {
        log.info("Removing item id: {} from purchase order id: {}", itemId, orderId);

        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(PURCHASE_ORDER_NOT_FOUND.getMessage()));

        if (order.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new InvalidOrderStatusException(ITEMS_CAN_ONLY_BE_REMOVED_FROM_DRAFT_ORDER.getMessage());
        }

        PurchaseOrderItem item = purchaseOrderItemRepository.findByIdAndPurchaseOrderId(itemId, orderId)
                .orElseThrow(() -> new NotFoundException(PURCHASE_ORDER_ITEM_NOT_FOUND.getMessage()));

        purchaseOrderItemRepository.delete(item);
    }


    @Override
    public PurchaseOrder getPurchaseOrderEntityById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PURCHASE_ORDER_NOT_FOUND.getMessage()));
    }

    @Override
    @Transactional
    public void confirmPurchaseOrderStatusFromDraft(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PURCHASE_ORDER_NOT_FOUND.getMessage()));

        if (order.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new InvalidOrderStatusException(ONLY_DRAFT_ORDERS_CAN_BE_CONFIRMED.getMessage());
        }

        order.setStatus(PurchaseOrderStatus.CONFIRMED);
        purchaseOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancelPurchaseOrderStatus(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PURCHASE_ORDER_NOT_FOUND.getMessage()));

        if (order.getStatus() == PurchaseOrderStatus.RECEIVED) {
            throw new InvalidOrderStatusException(RECEIVED_ORDERS_CANNOT_BE_CANCELLED.getMessage());
        }

        order.setStatus(PurchaseOrderStatus.CANCELLED);
        purchaseOrderRepository.save(order);
    }


    @Override
    @Transactional
    public void updateStatusFromInbound(Long id, PurchaseOrderStatus status) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PURCHASE_ORDER_NOT_FOUND.getMessage()));

        order.setStatus(status);
        purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder createForTransfer(Transfer transfer, List<TransferItem> items,
                                           Partner vendor, String username) {
        log.info("Creating PurchaseOrder for transfer: {}", transfer.getTransferNumber());

        User createdBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new MyException("User not found: " + username));

        Long nextVal = (Long) entityManager
                .createNativeQuery("SELECT NEXTVAL('purchase_order_number_seq')")
                .getSingleResult();
        String orderNumber = "PO-" + LocalDateTime.now().getYear() + "-" + String.format("%04d", nextVal);

        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .organization(transfer.getOrganization())
                .warehouse(transfer.getToLocation().getShelf().getZone().getWarehouse())
                .vendor(vendor)
                .createdBy(createdBy)
                .orderNumber(orderNumber)
                .transfer(transfer)
                .status(PurchaseOrderStatus.CONFIRMED)
                .expectedAt(LocalDateTime.now().plusDays(1))
                .notes("Auto-created for transfer: " + transfer.getTransferNumber())
                .build();

        purchaseOrderRepository.save(purchaseOrder);

        List<PurchaseOrderItem> purchaseOrderItems = items.stream()
                .map(item -> PurchaseOrderItem.builder()
                        .purchaseOrder(purchaseOrder)
                        .product(item.getProduct())
                        .quantity(item.getQuantity())
                        .unitPrice(BigDecimal.ZERO)             // internal transfer, no price
                        .build())
                .toList();

        purchaseOrderItemRepository.saveAll(purchaseOrderItems);

        log.info("PurchaseOrder created: {}", orderNumber);
        return purchaseOrder;
    }


    @Transactional
    public void cancelByTransfer(Transfer transfer) {
        log.info("Cancelling PurchaseOrder for transfer: {}", transfer.getTransferNumber());

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByTransfer(transfer)
                .orElseThrow(() -> new MyException("PurchaseOrder not found for transfer: " + transfer.getId()));

        if (purchaseOrder.getStatus() == PurchaseOrderStatus.RECEIVED)
            throw new MyException("Cannot cancel already received PurchaseOrder");

        purchaseOrder.setStatus(PurchaseOrderStatus.CANCELLED);
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public void addItemForTransfer(Transfer transfer, TransferItem item) {
        log.info("Adding item to PurchaseOrder for transfer: {}", transfer.getTransferNumber());

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByTransfer(transfer)
                .orElseThrow(() -> new MyException("PurchaseOrder not found for transfer: " + transfer.getId()));

        PurchaseOrderItem purchaseOrderItem = PurchaseOrderItem.builder()
                .purchaseOrder(purchaseOrder)
                .product(item.getProduct())
                .quantity(item.getQuantity())
                .unitPrice(BigDecimal.ZERO)
                .build();

        purchaseOrderItemRepository.save(purchaseOrderItem);
        log.info("PurchaseOrderItem added for product: {}", item.getProduct().getId());
    }


    @Transactional
    public void updateItemQuantityForTransfer(Transfer transfer, TransferItem item, Integer newQuantity) {
        log.info("Updating PurchaseOrderItem quantity for transfer: {}", transfer.getTransferNumber());

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByTransfer(transfer)
                .orElseThrow(() -> new MyException("PurchaseOrder not found for transfer: " + transfer.getId()));

        PurchaseOrderItem purchaseOrderItem = purchaseOrderItemRepository
                .findByPurchaseOrderAndProduct(purchaseOrder, item.getProduct())
                .orElseThrow(() -> new MyException("PurchaseOrderItem not found for product: " + item.getProduct().getId()));

        purchaseOrderItem.setQuantity(newQuantity);
        purchaseOrderItemRepository.save(purchaseOrderItem);
        log.info("PurchaseOrderItem quantity updated to: {}", newQuantity);
    }


    @Transactional
    public void removeItemForTransfer(Transfer transfer, TransferItem item) {
        log.info("Removing PurchaseOrderItem for transfer: {}", transfer.getTransferNumber());

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByTransfer(transfer)
                .orElseThrow(() -> new MyException("PurchaseOrder not found for transfer: " + transfer.getId()));

        PurchaseOrderItem purchaseOrderItem = purchaseOrderItemRepository
                .findByPurchaseOrderAndProduct(purchaseOrder, item.getProduct())
                .orElseThrow(() -> new MyException("PurchaseOrderItem not found for product: " + item.getProduct().getId()));

        purchaseOrderItemRepository.delete(purchaseOrderItem);
        log.info("PurchaseOrderItem removed for product: {}", item.getProduct().getId());
    }
}
