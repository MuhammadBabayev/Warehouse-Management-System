package az.microservice.werehouseapplication.model.dto.request.inboundReceipt;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateInboundReceiptDto {

    @NotNull(message = "Purchase order id cannot be empty")
    private Long purchaseOrderId;

    private Long userId;

    private String notes;

    @NotEmpty(message = "Receipt must have at least one item")
    private List<CreateInboundReceiptItemDto> items;
}
