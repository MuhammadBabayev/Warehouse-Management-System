package az.microservice.werehouseapplication.model.dto.request.product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateProductDto {
    private String name;
    private String description;
    private String unit;
    private String imageUrl;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private Integer minStockLevel;
    private BigDecimal weight;
    private Boolean isActive;
}
