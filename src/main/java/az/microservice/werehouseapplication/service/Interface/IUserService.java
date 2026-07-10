package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.user.AssignRoleRequest;
import az.microservice.werehouseapplication.model.dto.request.user.ChangePasswordDto;
import az.microservice.werehouseapplication.model.dto.request.user.UpdateUserDto;
import az.microservice.werehouseapplication.model.dto.request.user.UpdateUserStatusDto;
import az.microservice.werehouseapplication.model.dto.response.user.UserResponseDto;

import java.util.List;

public interface IUserService {
    List<UserResponseDto> getAll(String requesterUsername);
    UserResponseDto getById(Long id);
    UserResponseDto getMe(String username);
    UserResponseDto update(Long id, UpdateUserDto request);
    void changePassword(String username, ChangePasswordDto request);
    void delete(Long id);
    void updateStatus(Long id, UpdateUserStatusDto request);
    UserResponseDto assignRoleToUser(AssignRoleRequest request);
}
