package az.microservice.werehouseapplication.model.dto.request.outbound;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreatePickingDto {

    @NotNull(message = "Sales order id cannot be empty")
    private Long salesOrderId;

    @NotNull(message = "Assigned user id cannot be empty")
    private Long assignedToId;

    @NotEmpty(message = "Picking must have at least one item")
    private List<CreatePickingItemDto> items;
}