package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.SalesOrderStatus;
import az.microservice.werehouseapplication.exception.InvalidSalesOrderStatusException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.exception.UnauthorizedActionException;
import az.microservice.werehouseapplication.exception.old.MyException;
import az.microservice.werehouseapplication.model.dto.request.outbound.CreateSalesOrderDto;
import az.microservice.werehouseapplication.model.dto.request.outbound.CreateSalesOrderItemDto;
import az.microservice.werehouseapplication.model.dto.response.outbound.SalesOrderItemResponseDto;
import az.microservice.werehouseapplication.model.dto.response.outbound.SalesOrderResponseDto;
import az.microservice.werehouseapplication.model.entity.outbount.SalesOrder;
import az.microservice.werehouseapplication.model.entity.outbount.SalesOrderItem;
import az.microservice.werehouseapplication.model.entity.partner.Partner;
import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.model.entity.transfer.TransferItem;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import az.microservice.werehouseapplication.model.entity.warehouse.Warehouse;
import az.microservice.werehouseapplication.repository.*;
import az.microservice.werehouseapplication.service.Interface.IProductService;
import az.microservice.werehouseapplication.service.Interface.ISalesOrderService;
import az.microservice.werehouseapplication.service.Interface.IWarehouseService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesOrderService implements ISalesOrderService {
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final PartnerRepository partnerRepository;
    private final IWarehouseService warehouseService;
    private final IProductService productService;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final OrganizationRepository organizationRepository;


    @Override
    @Transactional
    public SalesOrderResponseDto create(CreateSalesOrderDto dto) {
        log.info("Creating sales order");

        User user = userRepository.findByIdAndItemStatus(dto.getUserId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        Organization organization = user.getOrganization();

        if (organization == null) {
            throw new UnauthorizedActionException(SUPER_ADMIN_CANNOT_CREATE_SALES_ORDER.getMessage());
        }

        Partner partner = partnerRepository.findById(dto.getPartnerId())
                .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND.getMessage()));

        Warehouse warehouse = warehouseService.getWarehouseEntityById(dto.getWarehouseId());

        String year = String.valueOf(LocalDateTime.now().getYear());

        String maxOrderNumber = salesOrderRepository.findMaxOrderNumberByYear(year)
                .orElse("SO-" + year + "-0000");

        int lastSequence = Integer.parseInt(maxOrderNumber.split("-")[2]);
        String sequence = String.format("%04d", lastSequence + 1);
        String orderNumber = "SO-" + year + "-" + sequence;

        SalesOrder order = SalesOrder.builder()
                .organization(organization)
                .partner(partner)
                .warehouse(warehouse)
                .createdBy(user)
                .orderNumber(orderNumber)
                .deliveryAddress(dto.getDeliveryAddress())
                .expectedDeliveryAt(dto.getExpectedDeliveryAt())
                .notes(dto.getNotes())
                .build();

        salesOrderRepository.save(order);

        List<SalesOrderItem> items = dto.getItems().stream()
                .map(itemDto -> {
                    Product product = productService.getProductEntityById(itemDto.getProductId());
                    return SalesOrderItem.builder()
                            .salesOrder(order)
                            .product(product)
                            .quantity(itemDto.getQuantity())
                            .unitPrice(itemDto.getUnitPrice())
                            .build();
                })
                .toList();

        salesOrderItemRepository.saveAll(items);

        return buildResponse(order, items);
    }

    @Override
    public SalesOrderResponseDto getById(Long id) {
        log.info("Fetching sales order with id: {}", id);

        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SALES_ORDER_NOT_FOUND.getMessage()));

        List<SalesOrderItem> items = salesOrderItemRepository.findAllBySalesOrderId(id);

        return buildResponse(order, items);
    }

    @Override
    public List<SalesOrderResponseDto> getAll() {
        log.info("Fetching all sales orders");

        List<SalesOrder> orders = salesOrderRepository.findAll();

        return orders.stream()
                .map(order -> buildResponse(order,
                        salesOrderItemRepository.findAllBySalesOrderId(order.getId())))
                .toList();
    }

    @Override
    public List<SalesOrderResponseDto> getAllByOrganizationId(Long organizationId){

//        Organization organization = organizationRepository.findByIdAndStatus(organizationId, ItemStatus.ACTIVE)
//                .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));

        List<SalesOrder> orders = salesOrderRepository.findAllByOrganizationId(organizationId);

        return orders.stream()
                .map(order -> buildResponse(order,
                        salesOrderItemRepository.findAllBySalesOrderId(order.getId())))
                .toList();
    }

    @Override
    @Transactional
    public void confirm(Long id) {
        log.info("Confirming sales order with id: {}", id);

        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SALES_ORDER_NOT_FOUND.getMessage()));

        if (order.getStatus() != SalesOrderStatus.DRAFT) {
            throw new InvalidSalesOrderStatusException(ONLY_DRAFT_ORDERS_CAN_BE_CONFIRMED.getMessage());
        }

        order.setStatus(SalesOrderStatus.CONFIRMED);
        salesOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        log.info("Cancelling sales order with id: {}", id);

        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SALES_ORDER_NOT_FOUND.getMessage()));

        if (order.getStatus() == SalesOrderStatus.SHIPPED ||
                order.getStatus() == SalesOrderStatus.DELIVERED) {
            throw new InvalidSalesOrderStatusException(SHIPPED_DELIVERED_ORDERS_CANNOT_CANCELLED.getMessage());
        }

        order.setStatus(SalesOrderStatus.CANCELLED);
        salesOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void addItem(Long orderId, CreateSalesOrderItemDto dto) {
        log.info("Adding item to sales order with id: {}", orderId);

        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(SALES_ORDER_NOT_FOUND.getMessage()));

        if (order.getStatus() != SalesOrderStatus.DRAFT) {
            throw new InvalidSalesOrderStatusException(ITEMS_CAN_ONLY_BE_ADDED_TO_DRAFT_ORDER.getMessage());
        }

        Product product = productService.getProductEntityById(dto.getProductId());

        SalesOrderItem item = SalesOrderItem.builder()
                .salesOrder(order)
                .product(product)
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .build();

        salesOrderItemRepository.save(item);
    }

    @Override
    @Transactional
    public void removeItem(Long orderId, Long itemId) {
        log.info("Removing item id: {} from sales order id: {}", itemId, orderId);

        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(SALES_ORDER_NOT_FOUND.getMessage()));

        if (order.getStatus() != SalesOrderStatus.DRAFT) {
            throw new InvalidSalesOrderStatusException(ITEMS_CAN_ONLY_BE_REMOVED_FROM_DRAFT_ORDER.getMessage());
        }

        SalesOrderItem item = salesOrderItemRepository.findByIdAndSalesOrderId(itemId, orderId)
                .orElseThrow(() -> new NotFoundException(SALES_ORDER_ITEM_NOT_FOUND.getMessage()));

        salesOrderItemRepository.delete(item);
    }

    @Override
    public SalesOrder getSalesOrderEntityById(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SALES_ORDER_NOT_FOUND.getMessage()));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, SalesOrderStatus status) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SALES_ORDER_NOT_FOUND.getMessage()));

        order.setStatus(status);
        salesOrderRepository.save(order);
    }

    @Transactional
    public SalesOrder createForTransfer(Transfer transfer, List<TransferItem> items,
                                        Partner customer, String username) {
        log.info("Creating SalesOrder for transfer: {}", transfer.getTransferNumber());

        User createdBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new MyException("User not found: " + username));

        Long nextVal = (Long) entityManager
                .createNativeQuery("SELECT NEXTVAL('sales_order_number_seq')")
                .getSingleResult();
        String orderNumber = "SO-" + LocalDateTime.now().getYear() + "-" + String.format("%04d", nextVal);

        SalesOrder salesOrder = SalesOrder.builder()
                .organization(transfer.getOrganization())
                .warehouse(transfer.getFromLocation().getShelf().getZone().getWarehouse())
                .partner(customer)
                .createdBy(createdBy)
                .orderNumber(orderNumber)
                .transfer(transfer)
                .status(SalesOrderStatus.CONFIRMED)
                .notes("Auto-created for transfer: " + transfer.getTransferNumber())
                .build();

        salesOrderRepository.save(salesOrder);

        List<SalesOrderItem> salesOrderItems = items.stream()
                .map(item -> SalesOrderItem.builder()
                        .salesOrder(salesOrder)
                        .product(item.getProduct())
                        .quantity(item.getQuantity())
                        .unitPrice(BigDecimal.ZERO)    // internal transfer, no price
                        .build())
                .toList();

        salesOrderItemRepository.saveAll(salesOrderItems);

        log.info("SalesOrder created: {}", orderNumber);
        return salesOrder;
    }


    @Transactional
    public void cancelByTransfer(Transfer transfer) {
        log.info("Cancelling SalesOrder for transfer: {}", transfer.getTransferNumber());

        SalesOrder salesOrder = salesOrderRepository.findByTransfer(transfer)
                .orElseThrow(() -> new MyException("SalesOrder not found for transfer: " + transfer.getId()));

        if (salesOrder.getStatus() == SalesOrderStatus.SHIPPED)
            throw new MyException("Cannot cancel already shipped SalesOrder");

        salesOrder.setStatus(SalesOrderStatus.CANCELLED);
        salesOrderRepository.save(salesOrder);
    }

    @Transactional
    public void addItemForTransfer(Transfer transfer, TransferItem item) {
        log.info("Adding item to SalesOrder for transfer: {}", transfer.getTransferNumber());

        SalesOrder salesOrder = salesOrderRepository.findByTransfer(transfer)
                .orElseThrow(() -> new MyException("SalesOrder not found for transfer: " + transfer.getId()));

        SalesOrderItem salesOrderItem = SalesOrderItem.builder()
                .salesOrder(salesOrder)
                .product(item.getProduct())
                .quantity(item.getQuantity())
                .unitPrice(BigDecimal.ZERO)
                .build();

        salesOrderItemRepository.save(salesOrderItem);
        log.info("SalesOrderItem added for product: {}", item.getProduct().getId());
    }


    @Transactional
    public void updateItemQuantityForTransfer(Transfer transfer, TransferItem item, Integer newQuantity) {
        log.info("Updating SalesOrderItem quantity for transfer: {}", transfer.getTransferNumber());

        SalesOrder salesOrder = salesOrderRepository.findByTransfer(transfer)
                .orElseThrow(() -> new MyException("SalesOrder not found for transfer: " + transfer.getId()));

        SalesOrderItem salesOrderItem = salesOrderItemRepository
                .findBySalesOrderAndProduct(salesOrder, item.getProduct())
                .orElseThrow(() -> new MyException("SalesOrderItem not found for product: " + item.getProduct().getId()));

        salesOrderItem.setQuantity(newQuantity);
        salesOrderItemRepository.save(salesOrderItem);
        log.info("SalesOrderItem quantity updated to: {}", newQuantity);
    }


    @Transactional
    public void removeItemForTransfer(Transfer transfer, TransferItem item) {
        log.info("Removing SalesOrderItem for transfer: {}", transfer.getTransferNumber());

        SalesOrder salesOrder = salesOrderRepository.findByTransfer(transfer)
                .orElseThrow(() -> new MyException("SalesOrder not found for transfer: " + transfer.getId()));

        SalesOrderItem salesOrderItem = salesOrderItemRepository
                .findBySalesOrderAndProduct(salesOrder, item.getProduct())
                .orElseThrow(() -> new MyException("SalesOrderItem not found for product: " + item.getProduct().getId()));

        salesOrderItemRepository.delete(salesOrderItem);
        log.info("SalesOrderItem removed for product: {}", item.getProduct().getId());
    }

    private SalesOrderResponseDto buildResponse(SalesOrder order, List<SalesOrderItem> items) {
        List<SalesOrderItemResponseDto> itemDtos = items.stream()
                .map(item -> SalesOrderItemResponseDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getUnitPrice().multiply(
                                BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        return SalesOrderResponseDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .customerName(order.getPartner().getName())
                .warehouseName(order.getWarehouse().getName())
                .organizationName(order.getOrganization().getName())
                .createdByUsername(order.getCreatedBy().getUsername())
                .deliveryAddress(order.getDeliveryAddress())
                .expectedDeliveryAt(order.getExpectedDeliveryAt())
                .createdAt(order.getCreatedAt())
                .notes(order.getNotes())
                .items(itemDtos)
                .build();
    }
}
