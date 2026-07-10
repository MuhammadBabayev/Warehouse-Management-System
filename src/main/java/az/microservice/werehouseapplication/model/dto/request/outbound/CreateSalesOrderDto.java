package az.microservice.werehouseapplication.model.dto.request.outbound;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CreateSalesOrderDto {

    @NotNull(message = "Customer id cannot be empty")
    private Long partnerId;

    @NotNull(message = "Warehouse id cannot be empty")
    private Long warehouseId;

    private String deliveryAddress;

    private Long userId;

    private LocalDateTime expectedDeliveryAt;

    private String notes;

    @NotEmpty(message = "Order must have at least one item")
    private List<CreateSalesOrderItemDto> items;
}