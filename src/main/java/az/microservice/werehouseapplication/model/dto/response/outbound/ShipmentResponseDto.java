package az.microservice.werehouseapplication.model.dto.response.outbound;


import az.microservice.werehouseapplication.enums.ShipmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponseDto {
    private Long id;
    private String trackingNumber;
    private ShipmentStatus status;
    private Long salesOrderId;
    private String salesOrderNumber;
    private String driverUsername;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private String notes;
}
