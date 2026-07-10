package az.microservice.werehouseapplication.model.dto.response.shelf;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShelfResponseDto {
    private Long id;
    private Long zoneId;
    private String zoneName;
    private String code;
    private String description;
    private LocalDateTime createdAt;
}
