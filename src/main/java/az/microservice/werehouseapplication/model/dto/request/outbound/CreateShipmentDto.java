package az.microservice.werehouseapplication.model.dto.request.outbound;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShipmentDto {

    @NotNull(message = "Sales order id cannot be empty")
    private Long salesOrderId;

    private Long driverId;

    private String notes;
}
