package az.microservice.werehouseapplication.model.dto.response.outbound;

import az.microservice.werehouseapplication.enums.PickingItemStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickingItemResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private Long locationId;
    private String locationCode;
    private Integer requiredQuantity;
    private Integer pickedQuantity;
    private PickingItemStatus status;
}
