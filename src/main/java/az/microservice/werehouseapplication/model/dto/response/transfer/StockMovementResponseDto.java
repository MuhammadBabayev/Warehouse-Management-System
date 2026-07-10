package az.microservice.werehouseapplication.model.dto.response.transfer;

import az.microservice.werehouseapplication.enums.MovementType;
import az.microservice.werehouseapplication.enums.ReferenceType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private Long locationId;
    private String locationCode;
    private String warehouseName;
    private String performedByUsername;
    private MovementType type;
    private Integer quantity;
    private Integer quantityBefore;
    private Integer quantityAfter;
    private ReferenceType referenceType;
    private Long referenceId;
    private String notes;
    private LocalDateTime createdAt;

}
