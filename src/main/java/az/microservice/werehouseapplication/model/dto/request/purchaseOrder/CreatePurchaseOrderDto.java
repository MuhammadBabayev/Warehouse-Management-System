package az.microservice.werehouseapplication.model.dto.request.purchaseOrder;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CreatePurchaseOrderDto {

    @NotNull(message = "Vendor id cannot be empty")
    private Long partnerId;

    @NotNull(message = "Warehouse id cannot be empty")
    private Long warehouseId;

    private LocalDateTime expectedAt;

    private Long userId;

    private String notes;

    @NotEmpty(message = "Order must have at least one item")
    private List<CreatePurchaseOrderItemDto> items;
}