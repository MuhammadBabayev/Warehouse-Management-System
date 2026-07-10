package az.microservice.werehouseapplication.model.dto.response.product;

import az.microservice.werehouseapplication.model.dto.response.barcode.BarcodeResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class ProductResponseDto {

    private Long id;
    private String name;
    private Integer minStock;
    private String imageUrl;
    private String brandName;
    private String categoryName;
    private String organizationName;
    private String unit;
    private String sku;
    private String description;
    private List<BarcodeResponseDto> barcode;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private BigDecimal weight;
    private Integer productCount;
}
