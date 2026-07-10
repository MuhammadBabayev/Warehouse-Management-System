package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.PickingItemStatus;
import az.microservice.werehouseapplication.enums.PickingStatus;
import az.microservice.werehouseapplication.enums.SalesOrderStatus;
import az.microservice.werehouseapplication.exception.InvalidOrderStatusException;
import az.microservice.werehouseapplication.exception.InvalidPickingStatusException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.exception.PickingItemsNotCompletedException;
import az.microservice.werehouseapplication.exception.old.MyException;
import az.microservice.werehouseapplication.model.dto.request.outbound.CompletePickingItemDto;
import az.microservice.werehouseapplication.model.dto.request.outbound.CreatePickingDto;
import az.microservice.werehouseapplication.model.dto.response.outbound.PickingItemResponseDto;
import az.microservice.werehouseapplication.model.dto.response.outbound.PickingResponseDto;
import az.microservice.werehouseapplication.model.entity.outbount.Picking;
import az.microservice.werehouseapplication.model.entity.outbount.PickingItem;
import az.microservice.werehouseapplication.model.entity.outbount.SalesOrder;
import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.model.entity.transfer.TransferItem;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;
import az.microservice.werehouseapplication.repository.PickingItemRepository;
import az.microservice.werehouseapplication.repository.PickingRepository;
import az.microservice.werehouseapplication.repository.SalesOrderRepository;
import az.microservice.werehouseapplication.repository.UserRepository;
import az.microservice.werehouseapplication.service.Interface.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PickingService implements IPickingService {

    private final PickingRepository pickingRepository;
    private final PickingItemRepository pickingItemRepository;
    private final UserRepository userRepository;
    private final ISalesOrderService salesOrderService;
    private final IProductService productService;
    private final ILocationService locationService;
    private final IInventoryService inventoryService;
    private final SalesOrderRepository salesOrderRepository;

    @Override
    @Transactional
    public PickingResponseDto create(CreatePickingDto dto) {
        log.info("Creating picking for sales order id: {}", dto.getSalesOrderId());

        SalesOrder salesOrder = salesOrderService.getSalesOrderEntityById(dto.getSalesOrderId());

        if (salesOrder.getStatus() != SalesOrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusException(INVALID_ORDER_STATUS_CONFIRMED.getMessage());
        }

        User assignedTo = userRepository.findById(dto.getAssignedToId())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        Picking picking = Picking.builder()
                .salesOrder(salesOrder)
                .assignedTo(assignedTo)
                .build();

        pickingRepository.save(picking);

        List<PickingItem> items = dto.getItems().stream()
                .map(itemDto -> {
                    Product product = productService.getProductEntityById(itemDto.getProductId());
                    Location location = locationService.getLocationEntityById(itemDto.getLocationId());

                    return PickingItem.builder()
                            .picking(picking)
                            .product(product)
                            .location(location)
                            .requiredQuantity(itemDto.getRequiredQuantity())
                            .pickedQuantity(0)
                            .build();
                })
                .toList();

        pickingItemRepository.saveAll(items);

        // SalesOrder statusunu PICKING-ə dəyiş
        salesOrderService.updateStatus(salesOrder.getId(), SalesOrderStatus.PICKING);

        return buildResponse(picking, items);
    }

    @Override
    public PickingResponseDto getById(Long id) {
        log.info("Fetching picking with id: {}", id);

        Picking picking = pickingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PICKING_NOT_FOUND.getMessage()));

        List<PickingItem> items = pickingItemRepository.findAllByPickingId(id);

        return buildResponse(picking, items);
    }

    @Override
    public List<PickingResponseDto> getAllBySalesOrderId(Long salesOrderId) {
        log.info("Fetching pickings for sales order id: {}", salesOrderId);

        return pickingRepository.findAllBySalesOrderId(salesOrderId)
                .stream()
                .map(picking -> buildResponse(picking,
                        pickingItemRepository.findAllByPickingId(picking.getId())))
                .toList();
    }

    @Override
    @Transactional
    public void start(Long id) {
        log.info("Starting picking with id: {}", id);

        Picking picking = pickingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PICKING_NOT_FOUND.getMessage()));

        if (picking.getStatus() != PickingStatus.PENDING) {
            throw new InvalidPickingStatusException(INVALID_PICKING_STATUS_PENDING.getMessage());
        }

        picking.setStatus(PickingStatus.IN_PROGRESS);
        picking.setStartedAt(LocalDateTime.now());
        pickingRepository.save(picking);
    }

    @Override
    @Transactional
    public void completeItem(Long pickingId, CompletePickingItemDto dto) {
        log.info("Completing picking item id: {} for picking id: {}", dto.getPickingItemId(), pickingId);

        Picking picking = pickingRepository.findById(pickingId)
                .orElseThrow(() -> new NotFoundException(PICKING_NOT_FOUND.getMessage()));

        if (picking.getStatus() != PickingStatus.IN_PROGRESS) {
            throw new InvalidPickingStatusException(INVALID_PICKING_STATUS_IN_PROGRESS.getMessage());
        }

        PickingItem item = pickingItemRepository.findByIdAndPickingId(dto.getPickingItemId(), pickingId)
                .orElseThrow(() -> new NotFoundException(PICKING_ITEM_NOT_FOUND.getMessage()));

        int pickedQty = dto.getPickedQuantity();
        item.setPickedQuantity(pickedQty);

        if (pickedQty >= item.getRequiredQuantity()) {
            item.setStatus(PickingItemStatus.PICKED);
        } else {
            item.setStatus(PickingItemStatus.SHORTAGE);
        }

        pickingItemRepository.save(item);

        // Inventory azalt
        inventoryService.decreaseStock(
                item.getProduct().getId(),
                item.getLocation().getId(),
                pickedQty
        );
    }

    @Override
    @Transactional
    public void complete(Long id) {
        log.info("Completing picking with id: {}", id);

        Picking picking = pickingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PICKING_NOT_FOUND.getMessage()));

        if (picking.getStatus() != PickingStatus.IN_PROGRESS) {
            throw new InvalidPickingStatusException(INVALID_PICKING_STATUS_FOR_COMPLETION.getMessage());
        }

        List<PickingItem> items = pickingItemRepository.findAllByPickingId(id);

        boolean anyPending = items.stream()
                .anyMatch(item -> item.getStatus() == PickingItemStatus.PENDING);

        if (anyPending) {
            throw new PickingItemsNotCompletedException(ALL_PICKING_ITEMS_MUST_BE_COMPLETED.getMessage());
        }

        picking.setStatus(PickingStatus.COMPLETED);
        picking.setCompletedAt(LocalDateTime.now());
        pickingRepository.save(picking);
    }

    @Transactional
    public Picking createForTransfer(Transfer transfer, List<TransferItem> items) {
        log.info("Creating Picking for transfer: {}", transfer.getTransferNumber());

        SalesOrder salesOrder = salesOrderRepository.findByTransfer(transfer)
                .orElseThrow(() -> new MyException("SalesOrder not found for transfer: " + transfer.getId()));

        User assignedTo = userRepository.findByUsername(transfer.getCreatedBy())
                .orElseThrow(() -> new MyException("User not found: " + transfer.getCreatedBy()));

        Picking picking = Picking.builder()
                .salesOrder(salesOrder)
                .assignedTo(assignedTo)
                .status(PickingStatus.PENDING)
                .build();

        pickingRepository.save(picking);

        List<PickingItem> pickingItems = items.stream()
                .map(item -> {
                    inventoryService.decreaseStock(
                            item.getProduct().getId(),
                            item.getFromLocation().getId(),
                            item.getQuantity()
                    );

                    return PickingItem.builder()
                            .picking(picking)
                            .product(item.getProduct())
                            .location(item.getFromLocation())
                            .requiredQuantity(item.getQuantity())
                            .pickedQuantity(item.getQuantity())
                            .status(PickingItemStatus.PICKED)
                            .build();
                }).toList();

        pickingItemRepository.saveAll(pickingItems);

        picking.setStatus(PickingStatus.COMPLETED);
        picking.setStartedAt(LocalDateTime.now());
        picking.setCompletedAt(LocalDateTime.now());
        pickingRepository.save(picking);

        log.info("Picking completed for transfer: {}", transfer.getTransferNumber());
        return picking;
    }

    private PickingResponseDto buildResponse(Picking picking, List<PickingItem> items) {
        List<PickingItemResponseDto> itemDtos = items.stream()
                .map(item -> PickingItemResponseDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .locationId(item.getLocation().getId())
                        .locationCode(item.getLocation().getCode())
                        .requiredQuantity(item.getRequiredQuantity())
                        .pickedQuantity(item.getPickedQuantity())
                        .status(item.getStatus())
                        .build())
                .toList();

        return PickingResponseDto.builder()
                .id(picking.getId())
                .salesOrderId(picking.getSalesOrder().getId())
                .salesOrderNumber(picking.getSalesOrder().getOrderNumber())
                .assignedToUsername(picking.getAssignedTo().getUsername())
                .status(picking.getStatus())
                .startedAt(picking.getStartedAt())
                .completedAt(picking.getCompletedAt())
                .createdAt(picking.getCreatedAt())
                .items(itemDtos)
                .build();
    }
}
