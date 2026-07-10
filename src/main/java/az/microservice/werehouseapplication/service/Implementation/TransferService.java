package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.*;
import az.microservice.werehouseapplication.exception.InternalServerError;
import az.microservice.werehouseapplication.exception.InvalidTransferStatusException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.exception.old.MyException;
import az.microservice.werehouseapplication.model.dto.request.transfer.*;
import az.microservice.werehouseapplication.model.dto.response.transfer.TransferItemResponseDto;
import az.microservice.werehouseapplication.model.dto.response.transfer.TransferResponseDto;
import az.microservice.werehouseapplication.model.entity.OutboxEvent;
import az.microservice.werehouseapplication.model.entity.partner.Partner;
import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.model.entity.transfer.TransferItem;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;
import az.microservice.werehouseapplication.repository.*;
import az.microservice.werehouseapplication.service.Interface.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService implements ITransferService {

    private final TransferRepository transferRepository;
    private final TransferItemRepository transferItemRepository;
    private final ILocationService locationService;
    private final IProductService productService;
    private final IInventoryService inventoryService;
    private final PartnerRepository partnerRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final IInvoiceService invoiceService;
    private final EntityManager entityManager;
    private final ISalesOrderService salesOrderService;
    private final IPickingService pickingService;
    private final IShipmentService shipmentService;
    private final IPurchaseOrderService purchaseOrderService;
    private final IInboundReceiptService inboundReceiptService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TransferResponseDto create(TransferRequestDto dto) {
        log.info("Creating transfer from: {} to: {}", dto.getFromLocationId(), dto.getToLocationId());

        User user = userRepository.findByIdAndItemStatus(dto.getUserId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        Partner vendor = partnerRepository.findByIdAndItemStatus(dto.getVendorId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(PARTNER_NOT_FOUND.getMessage()));

        Partner customer = partnerRepository.findByIdAndItemStatus(dto.getCustomerId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(PARTNER_NOT_FOUND.getMessage()));

        if (dto.getFromLocationId().equals(dto.getToLocationId()))
            throw new MyException("From and to locations cannot be the same");

        if (vendor.getStatus() == PartnerStatus.CUSTOMER)
            throw new MyException("Partner " + dto.getVendorId() + " is not a vendor");

        if (customer.getStatus() == PartnerStatus.VENDOR)
            throw new MyException("Partner " + dto.getCustomerId() + " is not a customer");

        Location fromLocation = locationService.getLocationEntityById(dto.getFromLocationId());
        Location toLocation = locationService.getLocationEntityById(dto.getToLocationId());

        // ✅ Validate stock availability BEFORE creating the transfer
        // Also reserve stock so the same units can't be used by another order
        for (TransferItemRequestDto itemDto : dto.getItems()) {
            int available = inventoryService.getStockQuantity(
                    itemDto.getProductId(), dto.getFromLocationId());

            if (available < itemDto.getQuantity()) {
                Product product = productService.getProductEntityById(itemDto.getProductId());
                throw new InternalServerError(
                        "Insufficient stock for product: " + product.getName()
                                + ". Available: " + available
                                + ", Requested: " + itemDto.getQuantity()
                );
            }
        }

        Long nextVal = (Long) entityManager
                .createNativeQuery("SELECT NEXTVAL('transfer_number_seq')")
                .getSingleResult();

        String transferNumber = "TRF-" + LocalDateTime.now().getYear()
                + "-" + String.format("%04d", nextVal);

        Transfer transfer = Transfer.builder()
                .organization(vendor.getOrganization())
                .fromLocation(fromLocation)
                .toLocation(toLocation)
                .customer(customer)
                .vendor(vendor)
                .createdBy(user.getUsername())
                .transferNumber(transferNumber)
                .notes(dto.getNotes())
                .build();

        transferRepository.save(transfer);

        List<TransferItem> items = dto.getItems().stream()
                .map(itemDto -> {
                    Product product = productService.getProductEntityById(itemDto.getProductId());
                    return TransferItem.builder()
                            .transfer(transfer)
                            .product(product)
                            .fromLocation(fromLocation)
                            .toLocation(toLocation)
                            .quantity(itemDto.getQuantity())
                            .build();
                }).toList();

        transferItemRepository.saveAll(items);

        // ✅ Reserve stock for each item so these units are locked
        // available → reserved (totalQuantity unchanged)
        items.forEach(item ->
                inventoryService.reserveStock(
                        item.getProduct().getId(),
                        item.getFromLocation().getId(),
                        item.getQuantity()
                )
        );

        salesOrderService.createForTransfer(transfer, items, customer, user.getUsername());
        purchaseOrderService.createForTransfer(transfer, items, vendor, user.getUsername());
        saveOutboxEvent(transfer, dto);

        return buildResponse(transfer, items);
    }


    @Override
    @Transactional
    public TransferResponseDto ship(Long id) {
        log.info("Shipping transfer: {}", id);

        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(TRANSFER_NOT_FOUND.getMessage()));

        if (transfer.getStatus() != TransferStatus.PENDING)
            throw new MyException("Only PENDING transfers can be shipped");

        List<TransferItem> items = transferItemRepository.findAllByTransferId(id);

        // ✅ Convert reserved → outbound: release reservation then decrease stock
        // This means units physically left the fromLocation
        items.forEach(item -> {
            // Step 1: release the reservation made during create()
            inventoryService.releaseReservedStock(
                    item.getProduct().getId(),
                    item.getFromLocation().getId(),
                    item.getQuantity()
            );
            // Step 2: physically remove from fromLocation inventory
            // Records OUTBOUND StockMovement automatically
            inventoryService.decreaseStock(
                    item.getProduct().getId(),
                    item.getFromLocation().getId(),
                    item.getQuantity()
            );
        });

        pickingService.createForTransfer(transfer, items);
        shipmentService.createForTransfer(transfer);

        transfer.setStatus(TransferStatus.SHIPPED);
        transfer.setShippedAt(LocalDateTime.now());
        transferRepository.save(transfer);

        return buildResponse(transfer, items);
    }


    @Override
    @Transactional
    public TransferResponseDto receive(Long id) {
        log.info("Receiving transfer: {}", id);

        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(TRANSFER_NOT_FOUND.getMessage()));

        if (transfer.getStatus() != TransferStatus.SHIPPED)
            throw new MyException("Only SHIPPED transfers can be received");

        List<TransferItem> items = transferItemRepository.findAllByTransferId(id);

        // ✅ Units physically arrived at toLocation
        // Records INBOUND StockMovement automatically
        // Auto-creates inventory row if product is new to this location
        items.forEach(item ->
                inventoryService.increaseStock(
                        item.getProduct().getId(),
                        item.getToLocation().getId(),
                        item.getQuantity()
                )
        );

        inboundReceiptService.createForTransfer(transfer, items);

        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setReceivedAt(LocalDateTime.now());
        transferRepository.save(transfer);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        invoiceService.generateCheck(transfer, InvoiceType.OUTGOING, username);
        invoiceService.generateCheck(transfer, InvoiceType.INCOMING, username);

        return buildResponse(transfer, items);
    }


    @Override
    @Transactional
    public void cancel(Long id) {
        log.info("Cancelling transfer with id: {}", id);

        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(TRANSFER_NOT_FOUND.getMessage()));

        if (transfer.getStatus() != TransferStatus.PENDING)
            throw new InvalidTransferStatusException(INVALID_TRANSFER_STATUS.getMessage());

        List<TransferItem> items = transferItemRepository.findAllByTransferId(id);

        // ✅ Release all reservations made during create()
        // reserved → available (totalQuantity unchanged, stock returns to normal)
        items.forEach(item ->
                inventoryService.releaseReservedStock(
                        item.getProduct().getId(),
                        item.getFromLocation().getId(),
                        item.getQuantity()
                )
        );

        salesOrderService.cancelByTransfer(transfer);
        purchaseOrderService.cancelByTransfer(transfer);

        transfer.setStatus(TransferStatus.CANCELLED);
        transferRepository.save(transfer);
    }


    @Override
    @Transactional
    public void addItem(Long transferId, TransferItemRequestDto dto) {
        log.info("Adding item to transfer with id: {}", transferId);

        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new NotFoundException(TRANSFER_NOT_FOUND.getMessage()));

        if (transfer.getStatus() != TransferStatus.PENDING)
            throw new InvalidTransferStatusException(INVALID_TRANSFER_STATUS.getMessage());

        // ✅ Check stock before adding
        int available = inventoryService.getStockQuantity(
                dto.getProductId(), transfer.getFromLocation().getId());

        if (available < dto.getQuantity()) {
            Product product = productService.getProductEntityById(dto.getProductId());
            throw new InternalServerError(
                    "Insufficient stock for product: " + product.getName()
                            + ". Available: " + available
                            + ", Requested: " + dto.getQuantity()
            );
        }

        Product product = productService.getProductEntityById(dto.getProductId());

        TransferItem item = TransferItem.builder()
                .transfer(transfer)
                .product(product)
                .fromLocation(transfer.getFromLocation())
                .toLocation(transfer.getToLocation())
                .quantity(dto.getQuantity())
                .build();

        transferItemRepository.save(item);

        // ✅ Reserve the newly added item's stock immediately
        inventoryService.reserveStock(
                product.getId(),
                transfer.getFromLocation().getId(),
                dto.getQuantity()
        );

        salesOrderService.addItemForTransfer(transfer, item);
        purchaseOrderService.addItemForTransfer(transfer, item);
    }


    @Override
    @Transactional
    public void updateItemQuantity(Long transferId, Long itemId, Integer newQuantity) {
        log.info("Updating item quantity for transfer id: {}, item id: {}", transferId, itemId);

        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new NotFoundException(TRANSFER_NOT_FOUND.getMessage()));

        if (transfer.getStatus() != TransferStatus.PENDING)
            throw new InvalidTransferStatusException(INVALID_TRANSFER_STATUS.getMessage());

        TransferItem item = transferItemRepository.findByIdAndTransferId(itemId, transferId)
                .orElseThrow(() -> new NotFoundException(TRANSFER_ITEM_NOT_FOUND.getMessage()));

        int oldQuantity = item.getQuantity();
        int diff = newQuantity - oldQuantity;

        if (diff > 0) {
            // ✅ Quantity increased — reserve the additional units
            int available = inventoryService.getStockQuantity(
                    item.getProduct().getId(), item.getFromLocation().getId());

            if (available < diff) {
                throw new InternalServerError(
                        "Insufficient stock to increase quantity by " + diff
                                + ". Available: " + available
                );
            }
            inventoryService.reserveStock(
                    item.getProduct().getId(),
                    item.getFromLocation().getId(),
                    diff
            );
        } else if (diff < 0) {
            // ✅ Quantity decreased — release the excess reservation
            inventoryService.releaseReservedStock(
                    item.getProduct().getId(),
                    item.getFromLocation().getId(),
                    Math.abs(diff)
            );
        }
        // diff == 0: nothing to do

        item.setQuantity(newQuantity);
        transferItemRepository.save(item);
        salesOrderService.updateItemQuantityForTransfer(transfer, item, newQuantity);
        purchaseOrderService.updateItemQuantityForTransfer(transfer, item, newQuantity);
    }


    @Override
    @Transactional
    public void removeItem(Long transferId, Long itemId) {
        log.info("Removing item id: {} from transfer id: {}", itemId, transferId);

        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new NotFoundException(TRANSFER_NOT_FOUND.getMessage()));

        if (transfer.getStatus() != TransferStatus.PENDING)
            throw new InvalidTransferStatusException(INVALID_TRANSFER_STATUS.getMessage());

        TransferItem item = transferItemRepository.findByIdAndTransferId(itemId, transferId)
                .orElseThrow(() -> new NotFoundException(TRANSFER_ITEM_NOT_FOUND.getMessage()));

        // ✅ Release reservation before removing the item
        inventoryService.releaseReservedStock(
                item.getProduct().getId(),
                item.getFromLocation().getId(),
                item.getQuantity()
        );

        transferItemRepository.delete(item);
        salesOrderService.removeItemForTransfer(transfer, item);
        purchaseOrderService.removeItemForTransfer(transfer, item);
    }


    // ── getById, getAll, buildResponse, saveOutboxEvent unchanged ──────────────

    @Override
    public TransferResponseDto getById(Long id) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(TRANSFER_NOT_FOUND.getMessage()));
        List<TransferItem> items = transferItemRepository.findAllByTransferId(id);
        return buildResponse(transfer, items);
    }

    @Override
    public List<TransferResponseDto> getAll() {
        return transferRepository.findAll().stream()
                .map(t -> buildResponse(t, transferItemRepository.findAllByTransferId(t.getId())))
                .toList();
    }

    private void saveOutboxEvent(Transfer transfer, TransferRequestDto dto) {
        try {
            TransferOutboxPayload payload = TransferOutboxPayload.builder()
                    .transferId(transfer.getId())
                    .fromLocationId(dto.getFromLocationId())
                    .toLocationId(dto.getToLocationId())
                    .vendorId(dto.getVendorId())
                    .customerId(dto.getCustomerId())
                    .items(dto.getItems().stream()
                            .map(i -> TransferOutboxPayload.TransferItemPayload.builder()
                                    .productId(i.getProductId())
                                    .quantity(i.getQuantity())
                                    .build())
                            .toList())
                    .build();

            OutboxEvent event = OutboxEvent.builder()
                    .eventType("TRANSFER_CREATED")
                    .payload(objectMapper.writeValueAsString(payload))
                    .build();

            outboxEventRepository.save(event);
            log.info("Outbox event saved for transfer: {}", transfer.getTransferNumber());
        } catch (Exception e) {
            log.error("Failed to save outbox event", e);
            throw new MyException("Failed to save outbox event");
        }
    }

    private TransferResponseDto buildResponse(Transfer transfer, List<TransferItem> items) {
        List<TransferItemResponseDto> itemDtos = items.stream()
                .map(item -> TransferItemResponseDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .fromLocationId(item.getFromLocation().getId())
                        .fromLocationCode(item.getFromLocation().getCode())
                        .toLocationId(item.getToLocation().getId())
                        .toLocationCode(item.getToLocation().getCode())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        return TransferResponseDto.builder()
                .id(transfer.getId())
                .transferNumber(transfer.getTransferNumber())
                .status(transfer.getStatus())
                .vendor(transfer.getVendor().getName())
                .customer(transfer.getCustomer().getName())
                .fromLocationId(transfer.getFromLocation().getId())
                .fromLocationCode(transfer.getFromLocation().getCode())
                .fromWarehouseName(transfer.getFromLocation().getShelf().getZone().getWarehouse().getName())
                .toLocationId(transfer.getToLocation().getId())
                .toLocationCode(transfer.getToLocation().getCode())
                .toWarehouseName(transfer.getToLocation().getShelf().getZone().getWarehouse().getName())
                .organizationName(transfer.getOrganization().getName())
                .createdBy(transfer.getCreatedBy())
                .notes(transfer.getNotes())
                .createdAt(transfer.getCreatedAt())
                .receivedAt(transfer.getReceivedAt())
                .shippedAt(transfer.getShippedAt())
                .completedAt(transfer.getCompletedAt())
                .items(itemDtos)
                .build();
    }
}