package az.microservice.werehouseapplication.model.dto.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignRoleRequest {
    private Long userId;
    private Long roleId;
    private Long assignedById;
}
