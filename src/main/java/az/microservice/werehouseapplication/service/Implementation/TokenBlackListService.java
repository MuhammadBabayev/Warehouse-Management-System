package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.model.entity.users.TokenBlackList;
import az.microservice.werehouseapplication.repository.TokenBlackListRepository;
import az.microservice.werehouseapplication.security.JwtUtil;
import az.microservice.werehouseapplication.service.Interface.ITokenBlackListService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenBlackListService implements ITokenBlackListService {
    private final JwtUtil jwtUtil;
    private final TokenBlackListRepository tokenBlackListRepository;

    public void blacklist(String token) {
        LocalDateTime expiresAt = jwtUtil.extractExpirationAsLocalDateTime(token);

        TokenBlackList blacklistedToken = TokenBlackList.builder()
                .token(token)
                .expiresAt(expiresAt)
                .build();

        tokenBlackListRepository.save(blacklistedToken);
    }

    public boolean isBlacklisted(String token) {
        return tokenBlackListRepository.existsByToken(token);
    }

    // Hər gecə saat 00:00-da köhnə tokenləri sil
    //Tokenin özü artıq expire olubsa blacklist-də saxlamağın mənası yoxdur —
    // çünki o token heç işləməyəcək. Ona görə hər gecə saat 00:00-da expiresAt < indi olan
    // bütün tokenləri cədvəldən silirik. Cədvəl şişmir.
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredTokens() {
        tokenBlackListRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
