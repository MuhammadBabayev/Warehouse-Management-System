package az.microservice.werehouseapplication.model.dto.response.inboundReceipt;
import az.microservice.werehouseapplication.enums.InboundReceiptStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboundReceiptResponseDto {
    private Long id;
    private String receiptNumber;
    private InboundReceiptStatus status;
    private Long purchaseOrderId;
    private String purchaseOrderNumber;
    private String receivedByUsername;
    private LocalDateTime receivedAt;
    private LocalDateTime createdAt;
    private String notes;
    private List<InboundReceiptItemResponseDto> items;
}
