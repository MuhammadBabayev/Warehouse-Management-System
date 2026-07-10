package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.model.entity.users.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByToken(String token);
//    boolean existsByEmailAndStatus(String email, InvitationStatus status);
}
