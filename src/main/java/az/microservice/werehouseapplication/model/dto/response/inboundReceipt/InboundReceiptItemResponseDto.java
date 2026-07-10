package az.microservice.werehouseapplication.model.dto.response.inboundReceipt;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboundReceiptItemResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private Long locationId;
    private String locationCode;
    private Integer expectedQuantity;
    private Integer receivedQuantity;
    private Integer rejectedQuantity;
    private String notes;
}
