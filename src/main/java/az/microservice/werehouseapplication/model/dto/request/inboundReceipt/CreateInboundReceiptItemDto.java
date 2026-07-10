package az.microservice.werehouseapplication.model.dto.request.inboundReceipt;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInboundReceiptItemDto {

    @NotNull(message = "Product id cannot be empty")
    private Long productId;

    @NotNull(message = "Location id cannot be empty")
    private Long locationId;

    @NotNull(message = "Expected quantity cannot be empty")
    private Integer expectedQuantity;

    @NotNull(message = "Received quantity cannot be empty")
    private Integer receivedQuantity;

    private Integer rejectedQuantity = 0;

    private String notes;
}
