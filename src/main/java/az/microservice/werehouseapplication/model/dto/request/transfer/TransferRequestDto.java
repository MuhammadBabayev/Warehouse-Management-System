package az.microservice.werehouseapplication.model.dto.request.transfer;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TransferRequestDto {
    @NotNull(message = "From location id cannot be empty")
    private Long fromLocationId;

    @NotNull(message = "To location id cannot be empty")
    private Long toLocationId;

    private Long vendorId;
    private Long customerId;
    private String notes;
    private Long userId;

    @NotEmpty(message = "Transfer must have at least one item")
    private List<TransferItemRequestDto> items;
}
