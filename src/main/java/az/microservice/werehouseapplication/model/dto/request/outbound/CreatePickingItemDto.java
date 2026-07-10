package az.microservice.werehouseapplication.model.dto.request.outbound;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePickingItemDto {

    @NotNull(message = "Product id cannot be empty")
    private Long productId;

    @NotNull(message = "Location id cannot be empty")
    private Long locationId;

    @NotNull(message = "Required quantity cannot be empty")
    private Integer requiredQuantity;
}
