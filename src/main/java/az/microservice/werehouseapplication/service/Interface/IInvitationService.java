package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.invitation.AcceptInvitationDto;
import az.microservice.werehouseapplication.model.dto.request.invitation.SendInvitationDto;

public interface IInvitationService {
    void acceptInvitation(AcceptInvitationDto request);
    void send(SendInvitationDto request, String senderUsername);
}
