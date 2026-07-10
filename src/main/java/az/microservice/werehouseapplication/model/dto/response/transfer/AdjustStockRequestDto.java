package az.microservice.werehouseapplication.model.dto.response.transfer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdjustStockRequestDto {
    Long productId;
    Long warehouseId;
    String adjustmentType;
    Integer delta;
}
