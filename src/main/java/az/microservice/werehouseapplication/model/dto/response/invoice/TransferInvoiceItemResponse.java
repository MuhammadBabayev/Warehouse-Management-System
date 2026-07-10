package az.microservice.werehouseapplication.model.dto.response.invoice;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferInvoiceItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private String barcode;
    private String unit;
    private Integer quantity;
    private BigDecimal unitPurchasePrice;
    private BigDecimal unitSellingPrice;
    private BigDecimal totalPrice;
    private String note;
}
