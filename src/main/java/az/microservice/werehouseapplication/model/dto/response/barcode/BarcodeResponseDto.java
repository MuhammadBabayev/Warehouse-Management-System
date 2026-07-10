package az.microservice.werehouseapplication.model.dto.response.barcode;

import az.microservice.werehouseapplication.enums.BarcodeType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarcodeResponseDto {
    private Long id;
    private String barcode;
    private BarcodeType type;
    private Integer quantity;
    private boolean isPrimary;
//    private Long productId;
//    private String productName;
}