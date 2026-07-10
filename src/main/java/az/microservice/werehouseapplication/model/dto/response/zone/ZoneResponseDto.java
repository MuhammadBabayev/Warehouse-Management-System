package az.microservice.werehouseapplication.model.dto.response.zone;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ZoneResponseDto {
    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
