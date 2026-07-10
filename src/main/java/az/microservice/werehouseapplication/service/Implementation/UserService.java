package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.exception.InvalidPasswordException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.exception.PasswordMatchException;
import az.microservice.werehouseapplication.model.dto.request.user.AssignRoleRequest;
import az.microservice.werehouseapplication.model.dto.request.user.ChangePasswordDto;
import az.microservice.werehouseapplication.model.dto.request.user.UpdateUserDto;
import az.microservice.werehouseapplication.model.dto.request.user.UpdateUserStatusDto;
import az.microservice.werehouseapplication.model.dto.response.user.UserResponseDto;
import az.microservice.werehouseapplication.model.entity.users.Role;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.users.UserRole;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import az.microservice.werehouseapplication.repository.OrganizationRepository;
import az.microservice.werehouseapplication.repository.RoleRepository;
import az.microservice.werehouseapplication.repository.UserRepository;
import az.microservice.werehouseapplication.repository.UserRoleRepository;
import az.microservice.werehouseapplication.service.Interface.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponseDto> getAll(String requesterUsername){

        User requester = findActiveUserByUsername(requesterUsername);

        // SUPER_ADMIN hamısını görür, ADMIN yalnız öz organizasiyasını
        List<User> users;
        if (requester.getOrganization() == null) {
            users = userRepository.findAllByItemStatus(ItemStatus.ACTIVE);
        } else {
            users = userRepository.findAllByOrganizationIdAndItemStatus(
                    requester.getOrganization().getId(), ItemStatus.ACTIVE);
        }

        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

    }

    @Override
    public UserResponseDto getById(Long id) {
        return toResponse(findActiveUserById(id));
    }

    @Override
    public UserResponseDto getMe(String username) {
        return toResponse(findActiveUserByUsername(username));
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UpdateUserDto request) {

        Organization organization = organizationRepository.findByIdAndStatus(request.getOrganizationId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));

        User user = userRepository.findByIdAndItemStatus(id, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setOrganization(organization);


        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto assignRoleToUser(AssignRoleRequest request){
        User user = findActiveUserById(request.getUserId());
        User assignedBy = findActiveUserById(request.getAssignedById());

        Role role = roleRepository.findByIdAndStatus(request.getRoleId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND.getMessage()));

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .assignedBy(assignedBy)
                .build();

        userRoleRepository.save(userRole);
        return toResponse(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = findActiveUserById(id);
        user.setItemStatus(ItemStatus.INACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, UpdateUserStatusDto request) {
        User user = findActiveUserById(id);
        user.setStatus(request.getStatus());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordDto request) {
        User user = findActiveUserByUsername(username);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException(INVALID_PASSWORD.getMessage());
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMatchException(PASSWORD_NOT_MATCH.getMessage());
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


    private User findActiveUserById(Long id) {
        return userRepository.findByIdAndItemStatus(id, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));
    }


    private User findActiveUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        if (user.getItemStatus() == ItemStatus.INACTIVE) {
            throw new NotFoundException(USER_NOT_FOUND.getMessage());
        }

        return user;
    }

    private UserResponseDto toResponse(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .organizationId(user.getOrganization() != null
                ? user.getOrganization().getId() : null)
                .organizationName(user.getOrganization() != null
                        ? user.getOrganization().getName() : null)
                .build();
    }
}
