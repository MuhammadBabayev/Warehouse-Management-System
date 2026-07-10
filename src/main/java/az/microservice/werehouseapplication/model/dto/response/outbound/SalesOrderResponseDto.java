package az.microservice.werehouseapplication.model.dto.response.outbound;


import az.microservice.werehouseapplication.enums.SalesOrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderResponseDto {
    private Long id;
    private String orderNumber;
    private SalesOrderStatus status;
    private String customerName;
    private String warehouseName;
    private String organizationName;
    private String createdByUsername;
    private String deliveryAddress;
    private LocalDateTime expectedDeliveryAt;
    private LocalDateTime createdAt;
    private String notes;
    private List<SalesOrderItemResponseDto> items;
}
