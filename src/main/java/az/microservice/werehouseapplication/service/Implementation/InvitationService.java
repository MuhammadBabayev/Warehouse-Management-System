package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.InvitationStatus;
import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.UserStatus;
import az.microservice.werehouseapplication.exception.*;
import az.microservice.werehouseapplication.model.dto.request.invitation.AcceptInvitationDto;
import az.microservice.werehouseapplication.model.dto.request.invitation.SendInvitationDto;
import az.microservice.werehouseapplication.model.entity.users.Invitation;
import az.microservice.werehouseapplication.model.entity.users.Role;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.users.UserRole;
import az.microservice.werehouseapplication.model.entity.warehouse.Warehouse;
import az.microservice.werehouseapplication.repository.*;
import az.microservice.werehouseapplication.service.Interface.IEmailService;
import az.microservice.werehouseapplication.service.Interface.IInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
public class InvitationService implements IInvitationService {
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final WarehouseRepository warehouseRepository;
    private final IEmailService emailService;

    @Override
    @Transactional
    public void acceptInvitation(AcceptInvitationDto request) {
        validatePasswords(request.getPassword(), request.getConfirmPassword());

        Invitation invitation = findValidInvitation(request.getToken());

        User user = userRepository.findByEmail(invitation.getEmail())
                .orElseThrow(() -> new NotFoundException(INVITATION_NOT_FOUND.getMessage()));

        activateUser(user, request.getPassword());
        assignRole(user, invitation);
        markInvitationAccepted(invitation);
    }

    @Override
    @Transactional
    public void send(SendInvitationDto request, String senderUsername){
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        //Validating new toUser
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistException(EMAIL_ALREADY_EXIST.getMessage());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AlreadyExistException(USERNAME_ALREADY_EXIST.getMessage());
        }

        Role role = roleRepository.findByIdAndStatus(request.getRoleId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND.getMessage()));

        Warehouse warehouse = null;
        if (request.getWarehouseId() != null) {
            warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new NotFoundException(WAREHOUSE_NOT_FOUND.getMessage()));
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .organization(sender.getOrganization())
                .status(UserStatus.INACTIVE)
                .build();
        userRepository.save(newUser);

        // Token yarat və invitation saxla
        String token = UUID.randomUUID().toString();
        Invitation invitation = Invitation.builder()
                .email(request.getEmail())
                .organization(sender.getOrganization())
                .role(role)
                .warehouse(warehouse)
                .token(token)
                .invitedBy(sender)
                .build();
        invitationRepository.save(invitation);

        emailService.sendInvitationEmail(request.getEmail(), token);
    }



    //region Private_kodlar
    private void validatePasswords(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new PasswordMismatchException(PASSWORD_MISMATCH.getMessage());
        }
    }



    private Invitation findValidInvitation(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException(INVITATION_NOT_FOUND.getMessage()));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new InvitationAlreadyProcessedException(INVITATION_ALREADY_PROCESSED.getMessage());
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
            throw new InvitationExpiredException(INVITATION_EXPIRED.getMessage());
        }

        return invitation;
    }

    private void activateUser(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    private void assignRole(User user, Invitation invitation) {
        if (!userRoleRepository.existsByUserAndRole(user, invitation.getRole())) {
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(invitation.getRole())
                    .assignedBy(invitation.getInvitedBy())
                    .build();
            userRoleRepository.save(userRole);
        }
    }

    private void markInvitationAccepted(Invitation invitation) {
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);
    }

    //endregion
}
