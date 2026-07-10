package az.microservice.werehouseapplication.model.dto.response.permission;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDto {
    private Long id;
    private String name;
    private String description;
//    private String status;
}
