package az.microservice.werehouseapplication.model.dto.response.location;

import az.microservice.werehouseapplication.enums.LocationStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponseDto {
    private Long id;
    private String code;
    private String description;
    private LocationStatus status;
    private Long maxCapacity;
    private Long shelfId;
    private String shelfCode;
}
