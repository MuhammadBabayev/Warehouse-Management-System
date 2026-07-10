package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.InvoiceStatus;
import az.microservice.werehouseapplication.enums.InvoiceType;
import az.microservice.werehouseapplication.enums.TransferStatus;
import az.microservice.werehouseapplication.exception.DuplicateInvoiceCheckException;
import az.microservice.werehouseapplication.exception.InvalidInvoiceStatusException;
import az.microservice.werehouseapplication.exception.InvoiceGenerationNotAllowedException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.model.dto.request.invoice.CheckActionRequest;
import az.microservice.werehouseapplication.model.dto.response.invoice.TransferInvoiceItemResponse;
import az.microservice.werehouseapplication.model.dto.response.invoice.TransferInvoiceResponse;
import az.microservice.werehouseapplication.model.entity.finance.Invoice;
import az.microservice.werehouseapplication.model.entity.finance.InvoiceItem;
import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.repository.InvoiceRepository;
import az.microservice.werehouseapplication.repository.TransferRepository;
import az.microservice.werehouseapplication.repository.UserRepository;
import az.microservice.werehouseapplication.service.Interface.IInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
public class InvoiceService implements IInvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final TransferRepository transferRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TransferInvoiceResponse generateCheck(Transfer transfer, InvoiceType invoiceType, String username) {

        if (transfer.getStatus() != TransferStatus.COMPLETED) {
            throw new InvoiceGenerationNotAllowedException(INVOICE_GENERATION_NOT_ALLOWED.getMessage());
        }

        if (invoiceRepository.existsByTransferIdAndInvoiceType(transfer.getId(), invoiceType)) {
            throw new DuplicateInvoiceCheckException(DUBLICATE_INVOICE_CHECK.getMessage());
        }

        User issuedBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        List<InvoiceItem> invoiceItems = transfer.getItems().stream()
                .map(item -> {
                    Product p = item.getProduct();
                    BigDecimal unitPrice = p.getPurchasePrice();
                    int qty = item.getQuantity();

                    return InvoiceItem.builder()
                            .product(p)
                            .productName(p.getName())
                            .productSku(p.getSku())
                            .barcode(p.getPrimaryBarcode())
                            .unit(p.getUnit())
                            .quantity(qty)
                            .unitPurchasePrice(unitPrice)
                            .unitSellingPrice(p.getSellingPrice())
                            .totalPrice(unitPrice.multiply(BigDecimal.valueOf(qty)))
                            .build();
                })
                .collect(Collectors.toList());

        int totalItems    = invoiceItems.size();
        int totalQuantity = invoiceItems.stream().mapToInt(InvoiceItem::getQuantity).sum();
        BigDecimal totalValue = invoiceItems.stream()
                .map(InvoiceItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Invoice check = Invoice.builder()
                .transfer(transfer)
                .organization(transfer.getOrganization())
                .issuedBy(issuedBy)
                .checkNumber(generateCheckNumber())
                .invoiceType(invoiceType)
                .fromWarehouseName(transfer.getFromLocation().getShelf().getZone().getWarehouse().getName())
                .fromLocationName(transfer.getFromLocation().getCode())
                .toWarehouseName(transfer.getToLocation().getShelf().getZone().getWarehouse().getName())
                .toLocationName(transfer.getToLocation().getCode())
                .totalItems(totalItems)
                .totalQuantity(totalQuantity)
                .totalValue(totalValue)
                .notes(null)
                .build();

        invoiceItems.forEach(i -> i.setInvoice(check));
        check.setItems(invoiceItems);

        Invoice saved = invoiceRepository.save(check);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public TransferInvoiceResponse getById(Long checkId) {
        Invoice check = invoiceRepository.findById(checkId)
                .orElseThrow(() -> new NotFoundException(INVOICE_NOT_FOUND.getMessage()));
        return toResponse(check);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransferInvoiceResponse> getByTransfer(Long transferId) {
        return invoiceRepository.findAllByTransferId(transferId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransferInvoiceResponse> getAllByOrganization(Long organizationId) {
        return invoiceRepository.findAllByOrganizationIdOrderByIssuedAtDesc(organizationId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransferInvoiceResponse issueInvoice(Long invoiceId, CheckActionRequest request) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException(INVOICE_NOT_FOUND.getMessage()));

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceStatusException(INVALID_INVOICE_STATUS_DRAFT.getMessage());
        }

        invoice.setStatus(InvoiceStatus.ISSUED);
        invoice.setIssuedAt(LocalDateTime.now());

        Invoice saved = invoiceRepository.save(invoice);
        return toResponse(saved);
    }

    @Override
    public TransferInvoiceResponse confirmCheck(Long invoiceId, CheckActionRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException(INVOICE_NOT_FOUND.getMessage()));

        if (invoice.getStatus() != InvoiceStatus.ISSUED) {
            throw new InvalidInvoiceStatusException(INVALID_INVOICE_STATUS_CONFIRMED.getMessage());
        }

        invoice.setStatus(InvoiceStatus.CONFIRMED);
        invoice.setConfirmedAt(LocalDateTime.now());
        if (request.getNotes() != null) {
            invoice.setNotes(invoice.getNotes() == null
                    ? request.getNotes()
                    : invoice.getNotes() + " | " + request.getNotes());
        }

        return toResponse(invoiceRepository.save(invoice));
    }

    @Override
    public TransferInvoiceResponse disputeCheck(Long invoiceId, CheckActionRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException(INVOICE_NOT_FOUND.getMessage()));

        if (invoice.getStatus() != InvoiceStatus.ISSUED) {
            throw new InvalidInvoiceStatusException(INVALID_INVOICE_STATUS_DISPUTED.getMessage());
        }

        invoice.setStatus(InvoiceStatus.DISPUTED);
        if (request.getNotes() != null) {
            invoice.setNotes(invoice.getNotes() == null
                    ? request.getNotes()
                    : invoice.getNotes() + " | DISPUTE: " + request.getNotes());
        }

        return toResponse(invoiceRepository.save(invoice));
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private String generateCheckNumber() {
        // CHK-2024-000042 — zero-padded 6-digit sequence
        String year = String.valueOf(LocalDateTime.now().getYear());
        long count  = invoiceRepository.count() + 1;
        return String.format("CHK-%s-%06d", year, count);
    }

    private TransferInvoiceResponse toResponse(Invoice c) {
        List<TransferInvoiceItemResponse> itemResponses = c.getItems().stream()
                .map(i -> TransferInvoiceItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProduct().getId())
                        .productName(i.getProductName())
                        .productSku(i.getProductSku())
                        .barcode(i.getBarcode())
                        .unit(i.getUnit())
                        .quantity(i.getQuantity())
                        .unitPurchasePrice(i.getUnitPurchasePrice())
                        .unitSellingPrice(i.getUnitSellingPrice())
                        .totalPrice(i.getTotalPrice())
                        .note(i.getNote())
                        .build())
                .collect(Collectors.toList());

        return TransferInvoiceResponse.builder()
                .id(c.getId())
                .checkNumber(c.getCheckNumber())
                .invoiceType(c.getInvoiceType())
                .status(c.getStatus())
                .transferNumber(c.getTransfer().getTransferNumber())
                .transferStatus(c.getTransfer().getStatus())
                .organizationName(c.getOrganization().getName())
                .fromWarehouseName(c.getFromWarehouseName())
                .fromLocationName(c.getFromLocationName())
                .toWarehouseName(c.getToWarehouseName())
                .toLocationName(c.getToLocationName())
                .issuedByFullName(c.getIssuedBy().getUsername())
                .issuedAt(c.getIssuedAt())
                .confirmedAt(c.getConfirmedAt())
                .totalItems(c.getTotalItems())
                .totalQuantity(c.getTotalQuantity())
                .totalValue(c.getTotalValue())
                .notes(c.getNotes())
                .items(itemResponses)
                .build();
    }
}
