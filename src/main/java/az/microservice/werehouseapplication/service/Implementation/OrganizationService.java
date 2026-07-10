package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.exception.AlreadyExistException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.mapper.OrganizationMapper;
import az.microservice.werehouseapplication.model.dto.request.organization.CreateOrganizationDto;
import az.microservice.werehouseapplication.model.dto.request.organization.UpdateOrganizationDto;
import az.microservice.werehouseapplication.model.dto.response.organization.OrganizationResponseDto;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import az.microservice.werehouseapplication.repository.*;
import az.microservice.werehouseapplication.service.Interface.IOrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
public class OrganizationService implements IOrganizationService {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final OrganizationMapper organizationMapper;
    private final UserRoleRepository userRoleRepository;


    @Transactional
    public OrganizationResponseDto create(CreateOrganizationDto request) {
        validateOrganizationName(request.getName());

        User user = userRepository.findByIdAndItemStatus(request.getUserId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        validateUserIsAdmin(user);

        Organization savedOrg;

        Optional<Organization> inactiveOrganization = organizationRepository
                .findByEmailAndStatus(request.getEmail(), ItemStatus.INACTIVE);

        if (inactiveOrganization.isPresent()) {
            Organization existing = inactiveOrganization.get();
            existing.setStatus(ItemStatus.ACTIVE);
            existing.setName(request.getName());
            existing.setAddress(request.getAddress());
            existing.setPhone(request.getPhone());
            savedOrg = organizationRepository.save(existing);
        } else {
            Organization organization = organizationMapper.toEntity(request);
            savedOrg = organizationRepository.save(organization);
        }

        user.setOrganization(savedOrg);
        userRepository.save(user);

        return organizationMapper.toResponseDto(savedOrg);
    }

    @Override
    public OrganizationResponseDto getById(Long id) {
        return organizationMapper.toResponseDto(findOrganizationById(id));
    }


    @Override
    public List<OrganizationResponseDto> getAll() {
        return organizationRepository.findAll()
                .stream()
                .map(organizationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrganizationResponseDto update(Long id, UpdateOrganizationDto request) {
        Organization organization = findOrganizationById(id);

        if (!organization.getName().equals(request.getName())) {
            validateOrganizationName(request.getName());
        }

        organization.setName(request.getName());
        organization.setAddress(request.getAddress());
        organization.setPhone(request.getPhone());
        organization.setEmail(request.getEmail());
        organization.setUpdatedAt(LocalDateTime.now());

        return organizationMapper.toResponseDto(organizationRepository.save(organization));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Organization organization = findOrganizationById(id);
        organization.setStatus(ItemStatus.INACTIVE);
        organizationRepository.save(organization);
    }

    // HELPER METHODS!
        private void validateOrganizationName(String name) {
            if (organizationRepository.existsByName(name)) {
                throw new AlreadyExistException(ORGANIZATION_ALREADY_EXIST.getMessage());
            }
        }

        private Organization findOrganizationById(Long id) {
            return organizationRepository.findByIdAndStatus(id, ItemStatus.ACTIVE)
                    .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));
        }

    private void validateUserIsAdmin(User user) {
        boolean isAdmin = userRoleRepository.existsByUserAndRole_Name(user, "ADMIN");
        if (!isAdmin) {
            throw new AccessDeniedException(USER_IS_NOT_ADMIN.getMessage());
        }
    }

}
