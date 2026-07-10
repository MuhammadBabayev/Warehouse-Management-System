package az.microservice.werehouseapplication.model.dto.response.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class InventoryResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private Long locationId;
    private String locationCode;
    private String shelfCode;
    private String zoneName;
    private String warehouseName;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer damagedQuantity;
    private Integer totalQuantity;
}
