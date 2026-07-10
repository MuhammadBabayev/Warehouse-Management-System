package az.microservice.werehouseapplication.model.dto.request.purchaseOrder;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePurchaseOrderItemDto {

    @NotNull(message = "Product id cannot be empty")
    private Long productId;

    @NotNull(message = "Quantity cannot be empty")
    private Integer quantity;

    @NotNull(message = "Unit price cannot be empty")
    private BigDecimal unitPrice;
}