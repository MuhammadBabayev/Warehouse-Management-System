package az.microservice.werehouseapplication.model.dto.response.user;

import az.microservice.werehouseapplication.enums.UserStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String username;
    private String lastName;
    private String email;
    private String phone;
    private UserStatus status;
    private String organizationName;
    private Long organizationId;
}
