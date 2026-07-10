package az.microservice.werehouseapplication.model.dto.response.role;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponseDto {
    private Long id;
    private String name;
    private List<String> permissions;
}
