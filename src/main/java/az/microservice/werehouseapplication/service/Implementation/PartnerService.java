package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.exception.AlreadyExistException;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.model.dto.request.partner.CreatePartnerDto;
import az.microservice.werehouseapplication.model.dto.request.partner.UpdatePartnerDto;
import az.microservice.werehouseapplication.model.dto.response.partner.PartnerResponseDto;
import az.microservice.werehouseapplication.model.entity.partner.Partner;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import az.microservice.werehouseapplication.repository.OrganizationRepository;
import az.microservice.werehouseapplication.repository.PartnerRepository;
import az.microservice.werehouseapplication.service.Interface.IPartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerService implements IPartnerService {

    private final PartnerRepository partnerRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public PartnerResponseDto create(CreatePartnerDto dto) {
        log.info("Creating Partner with name: {}", dto.getName());

        Organization organization = organizationRepository.findByIdAndStatus(dto.getOrganizationId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));

        if (partnerRepository.existsByNameAndOrganization(dto.getName(), organization)) {
            throw new AlreadyExistException(PARTNER_ALREADY_EXIST.getMessage());
        }

        Partner partner = Partner.builder()
                .name(dto.getName())
                .contactPerson(dto.getContactPerson())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .organization(organization)
                .status(dto.getStatus())
                .build();

        Partner saved = partnerRepository.save(partner);
        return toResponse(saved);
    }

    @Override
    public PartnerResponseDto getById(Long id) {
        log.info("Fetching Partner with id: {}", id);

        Partner partner = findActivePartnerById(id);
        return toResponse(partner);
    }

    @Override
    public List<PartnerResponseDto> getAll() {
        log.info("Fetching all Partners");
        List<Partner> partners = partnerRepository.findAllByItemStatus(ItemStatus.ACTIVE);

        return partners.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PartnerResponseDto update(Long id, UpdatePartnerDto dto) {
        log.info("Updating Partner with id: {}", id);

        Partner partner = findActivePartnerById(id);

        if (dto.getName() != null && !dto.getName().equals(partner.getName())) {
            if (partnerRepository.existsByNameAndOrganization(dto.getName(), partner.getOrganization())) {
                throw new AlreadyExistException(PARTNER_ALREADY_EXIST.getMessage());
            }
            partner.setName(dto.getName());
        }

        if (dto.getContactPerson() != null) partner.setContactPerson(dto.getContactPerson());
        if (dto.getPhone() != null) partner.setPhone(dto.getPhone());
        if (dto.getEmail() != null) partner.setEmail(dto.getEmail());
        if (dto.getAddress() != null) partner.setAddress(dto.getAddress());

        Partner updated = partnerRepository.save(partner);

        return toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting Partner with id: {}", id);
        Partner partner = findActivePartnerById(id);

        partner.setItemStatus(ItemStatus.INACTIVE);
        partnerRepository.save(partner);
    }

    @Override
    public Partner getPartnerEntityById(Long id) {
        return partnerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PARTNER_NOT_FOUND.getMessage()));
    }

    private Partner findActivePartnerById(Long id){
        return partnerRepository.findByIdAndItemStatus(id, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(PARTNER_NOT_FOUND.getMessage()));
    }

    private PartnerResponseDto toResponse(Partner partner){
        return PartnerResponseDto.builder()
                .id(partner.getId())
                .name(partner.getName())
                .contactPerson(partner.getContactPerson())
                .phone(partner.getPhone())
                .email(partner.getEmail())
                .address(partner.getAddress())
                .status(partner.getStatus())
                .organizationName(partner.getOrganization().getName())
                .build();
    }

}

