package az.microservice.werehouseapplication.model.dto.request.product;

import az.microservice.werehouseapplication.model.dto.request.barcode.CreateBarcodeDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateProductDto{

    @NotBlank(message = "Product name cannot be empty")
    private String name;

    private String description;

    private String unit;

    private BigDecimal weight;

    private Integer minStockLevel;

    private String imageUrl;

    @NotNull(message = "Purchase price cannot be empty")
    private BigDecimal purchasePrice;

    @NotNull(message = "Selling price cannot be empty")
    private BigDecimal sellingPrice;

    private Long organizationId;

    private Long brandId;

    private Long categoryId;

    private Integer productCount;

    private List<CreateBarcodeDto> barcodes;

    //organizationId — lazım deyil. Brand-da izah etdiyimiz kimi,
    // organizasiyanı tokendən götürürük. Frontend-dən göndərmək təhlükəsizlik açığıdır

}
