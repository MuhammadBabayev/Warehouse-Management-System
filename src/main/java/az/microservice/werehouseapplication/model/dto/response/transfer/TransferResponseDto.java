package az.microservice.werehouseapplication.model.dto.response.transfer;

import az.microservice.werehouseapplication.enums.TransferStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponseDto {
    private Long id;
    private String transferNumber;
    private TransferStatus status;
    private Long fromLocationId;
    private String fromLocationCode;
    private String fromWarehouseName;
    private Long toLocationId;
    private String toLocationCode;
    private String toWarehouseName;
    private String organizationName;
    private String vendor;
    private String customer;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime receivedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime completedAt;
    private String notes;
    private List<TransferItemResponseDto> items;

}
