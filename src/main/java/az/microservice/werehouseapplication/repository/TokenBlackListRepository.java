package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.users.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, Long> {
    boolean existsByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM TokenBlackList t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
}
