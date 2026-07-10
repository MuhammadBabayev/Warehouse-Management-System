package az.microservice.werehouseapplication.model.dto.request.transfer;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferItemRequestDto {
    @NotNull(message = "Product id cannot be empty")
    private Long productId;

    @NotNull(message = "Quantity cannot be empty")
    private Integer quantity;
}
