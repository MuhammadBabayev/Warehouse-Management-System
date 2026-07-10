package az.microservice.werehouseapplication.model.dto.response.transfer;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferItemResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private Long fromLocationId;
    private String fromLocationCode;
    private Long toLocationId;
    private String toLocationCode;
    private Integer quantity;
}
