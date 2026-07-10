package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.UserStatus;
import az.microservice.werehouseapplication.model.dto.request.authentication.LoginRequestDto;
import az.microservice.werehouseapplication.model.dto.request.authentication.RegisterRequestDto;
import az.microservice.werehouseapplication.model.dto.response.authentication.AuthResponseDto;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.repository.UserRepository;
import az.microservice.werehouseapplication.security.JwtUtil;
import az.microservice.werehouseapplication.service.Interface.IAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        User user = User.builder()
                .username(request.getUsername())
                .phone(request.getPhone())
                .email(request.getEmail())
                .status(UserStatus.PENDING)
                .password(passwordEncoder.encode(request.getPassword()))

                .build();

        userRepository.save(user);

        var userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponseDto(token, user.getUsername(), user.getEmail());
    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("username or password is incorrect");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("user not found with username: " + request.getUsername() + ""));

        String token = jwtUtil.generateToken(userDetails);

        return AuthResponseDto.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}