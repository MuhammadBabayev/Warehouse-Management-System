package az.microservice.werehouseapplication.model.dto.request.user;

import az.microservice.werehouseapplication.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserStatusDto {

    @NotNull(message = "Status cannot be empty")
    private UserStatus status;
}
