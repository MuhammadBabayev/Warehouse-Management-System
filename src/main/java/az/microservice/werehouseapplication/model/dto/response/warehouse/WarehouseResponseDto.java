package az.microservice.werehouseapplication.model.dto.response.warehouse;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WarehouseResponseDto {
    private Long id;
    private Long organizationId;
    private String organizationName;
    private String name;
    private String phone;
    private String address;
    private LocalDateTime createdAt;
}
