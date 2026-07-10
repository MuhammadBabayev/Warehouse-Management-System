package az.microservice.werehouseapplication.model.dto.response.purchaseOrder;

import az.microservice.werehouseapplication.enums.PurchaseOrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponseDto {
    private Long id;
    private String orderNumber;
    private PurchaseOrderStatus status;
    private String vendorName;
    private String warehouseName;
    private String organizationName;
    private String createdByUsername;
    private LocalDateTime expectedAt;
    private LocalDateTime createdAt;
    private String notes;
    private List<PurchaseOrderItemResponseDto> items;
}
