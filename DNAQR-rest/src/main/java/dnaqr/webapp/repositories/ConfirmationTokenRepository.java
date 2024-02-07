package dnaqr.webapp.repositories;

import dnaqr.webapp.entities.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByConfirmationToken(UUID confirmationToken);
}
