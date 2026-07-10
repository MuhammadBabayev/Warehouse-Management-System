package az.microservice.werehouseapplication.model.dto.response.transfer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ProductStockSummaryDto {
    private Long productId;
    private String productName;
    private String sku;
    private int totalAvailable;
    private int totalReserved;
    private int totalDamaged;
    private int totalInWarehouses;
    private int unassignedStock;
    private int locationCount;
}
