package az.microservice.werehouseapplication.model.dto.request.location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLocationDto {
    private Long shelfId;
    private String code;
    private String description;
    private Long maxCapacity;
}
