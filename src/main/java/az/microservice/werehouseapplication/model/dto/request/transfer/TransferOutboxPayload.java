package az.microservice.werehouseapplication.model.dto.request.transfer;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferOutboxPayload {
    private Long transferId;
    private Long fromLocationId;
    private Long toLocationId;
    private Long vendorId;
    private Long customerId;
    private List<TransferItemPayload> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransferItemPayload {
        private Long productId;
        private Integer quantity;
    }
}