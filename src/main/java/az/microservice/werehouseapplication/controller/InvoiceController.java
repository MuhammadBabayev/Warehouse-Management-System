package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.invoice.CheckActionRequest;
import az.microservice.werehouseapplication.model.dto.response.invoice.TransferInvoiceResponse;
import az.microservice.werehouseapplication.service.Implementation.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{id}")
    public ResponseEntity<TransferInvoiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @GetMapping("/transfer/{transferId}")
    public ResponseEntity<List<TransferInvoiceResponse>> getByTransfer(
            @PathVariable Long transferId) {
        return ResponseEntity.ok(invoiceService.getByTransfer(transferId));
    }

    @GetMapping("/organization/{orgId}")
    public ResponseEntity<List<TransferInvoiceResponse>> getByOrganization(
            @PathVariable Long orgId) {
        return ResponseEntity.ok(invoiceService.getAllByOrganization(orgId));
    }

    @PatchMapping("/{id}/issue")
    public ResponseEntity<TransferInvoiceResponse> issue(@PathVariable Long id, @RequestBody CheckActionRequest request) {
        return ResponseEntity.ok(invoiceService.issueInvoice(id, request));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<TransferInvoiceResponse> confirm(
            @PathVariable Long id,
            @RequestBody CheckActionRequest request) {
        return ResponseEntity.ok(invoiceService.confirmCheck(id, request));
    }

    @PatchMapping("/{id}/dispute")
    public ResponseEntity<TransferInvoiceResponse> dispute(
            @PathVariable Long id,
            @RequestBody CheckActionRequest request) {
        return ResponseEntity.ok(invoiceService.disputeCheck(id, request));
    }
}
