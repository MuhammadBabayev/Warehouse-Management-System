package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.user.AssignRoleRequest;
import az.microservice.werehouseapplication.model.dto.request.user.ChangePasswordDto;
import az.microservice.werehouseapplication.model.dto.request.user.UpdateUserDto;
import az.microservice.werehouseapplication.model.dto.request.user.UpdateUserStatusDto;
import az.microservice.werehouseapplication.model.dto.response.user.UserResponseDto;
import az.microservice.werehouseapplication.service.Interface.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PatchMapping("/role")
    @PreAuthorize("hasAuthority('user.update')")
    public ResponseEntity<UserResponseDto> assignRoleToUser(@RequestBody AssignRoleRequest request){
        return ResponseEntity.ok(userService.assignRoleToUser(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user.view')")
    public ResponseEntity<List<UserResponseDto>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getAll(userDetails.getUsername()));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getMe(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getMe(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user.view')")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user.update')")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id,
                                                  @RequestBody UpdateUserDto request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('user.update')")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,
                                             @Valid @RequestBody UpdateUserStatusDto request) {
        userService.updateStatus(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordDto request) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.noContent().build();
    }
}