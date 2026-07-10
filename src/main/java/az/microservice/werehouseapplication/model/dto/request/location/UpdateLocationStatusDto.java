package az.microservice.werehouseapplication.model.dto.request.location;

import az.microservice.werehouseapplication.enums.LocationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLocationStatusDto {

    private LocationStatus status; // active, inactive, full
}