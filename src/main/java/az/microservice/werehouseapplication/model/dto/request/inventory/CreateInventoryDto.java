package az.microservice.werehouseapplication.model.dto.request.inventory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateInventoryDto {
    Long productId;
    Long warehouseId;
    Integer availableQuantity;
    Integer reservedQuantity;
    Integer damagedQuantity;
}
