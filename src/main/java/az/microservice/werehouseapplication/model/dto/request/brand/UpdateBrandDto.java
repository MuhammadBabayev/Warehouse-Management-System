package az.microservice.werehouseapplication.model.dto.request.brand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBrandDto {
    private String name;
    private String description;
}

