package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.authentication.LoginRequestDto;
import az.microservice.werehouseapplication.model.dto.request.authentication.RegisterRequestDto;
import az.microservice.werehouseapplication.model.dto.request.invitation.AcceptInvitationDto;
import az.microservice.werehouseapplication.model.dto.response.authentication.AuthResponseDto;
import az.microservice.werehouseapplication.service.Interface.IAuthService;
import az.microservice.werehouseapplication.service.Interface.IInvitationService;
import az.microservice.werehouseapplication.service.Interface.ITokenBlackListService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final IInvitationService invitationService;
    private final ITokenBlackListService tokenBlackListService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    //User POST /api/v1/auth/logout çağıranda — header-dəki tokeni götürür
    // → TokenBlacklistService.blacklist() çağırır → cədvələ yazır.
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlackListService.blacklist(token);
        }

        return ResponseEntity.ok("Uğurla çıxış edildi.");
    }


    @PostMapping("/accept-invitation")
    public ResponseEntity<Void> acceptInvitation(@Valid @RequestBody AcceptInvitationDto request) {
        invitationService.acceptInvitation(request);
        return ResponseEntity.noContent().build();
    }



    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }




}
