package az.microservice.werehouseapplication.model.dto.response.outbound;
import az.microservice.werehouseapplication.enums.PickingStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickingResponseDto {
    private Long id;
    private Long salesOrderId;
    private String salesOrderNumber;
    private String assignedToUsername;
    private PickingStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private List<PickingItemResponseDto> items;
}
