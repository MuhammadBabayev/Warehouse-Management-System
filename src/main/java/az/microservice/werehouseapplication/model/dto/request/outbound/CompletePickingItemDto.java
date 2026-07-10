package az.microservice.werehouseapplication.model.dto.request.outbound;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompletePickingItemDto {

    @NotNull(message = "Picking item id cannot be empty")
    private Long pickingItemId;

    @NotNull(message = "Picked quantity cannot be empty")
    private Integer pickedQuantity;
}
