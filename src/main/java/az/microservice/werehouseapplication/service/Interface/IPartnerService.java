package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.partner.CreatePartnerDto;
import az.microservice.werehouseapplication.model.dto.request.partner.UpdatePartnerDto;
import az.microservice.werehouseapplication.model.dto.response.partner.PartnerResponseDto;
import az.microservice.werehouseapplication.model.entity.partner.Partner;

import java.util.List;

public interface IPartnerService {
    PartnerResponseDto create(CreatePartnerDto dto);
    PartnerResponseDto getById(Long id);
    List<PartnerResponseDto> getAll();
    PartnerResponseDto update(Long id, UpdatePartnerDto dto);
    void delete(Long id);
    Partner getPartnerEntityById(Long id);
}
