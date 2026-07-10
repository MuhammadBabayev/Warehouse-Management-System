package az.microservice.werehouseapplication.model.dto.response.inventory;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InventoryQuantityResponseDto {
    private Long inventoryId;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer damagedQuantity;
    private Integer totalQuantity;
}
