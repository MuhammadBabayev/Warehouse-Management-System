package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.invitation.AcceptInvitationDto;
import az.microservice.werehouseapplication.model.dto.request.invitation.SendInvitationDto;
import az.microservice.werehouseapplication.service.Interface.IInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {
    private final IInvitationService invitationService;


    //Admin ve ya SuperAdmin Invitation gonderir
    @PostMapping("/send")
    @PreAuthorize("hasAuthority('user.create')")
    public ResponseEntity<Void> sendInvitation(@Valid @RequestBody SendInvitationDto request,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        invitationService.send(request, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/accept")
    public ResponseEntity<String> acceptInvitation(
            @RequestBody @Valid AcceptInvitationDto dto) {
        invitationService.acceptInvitation(dto);
        return ResponseEntity.ok("you signed up successfully, now you can login");
    }




}
