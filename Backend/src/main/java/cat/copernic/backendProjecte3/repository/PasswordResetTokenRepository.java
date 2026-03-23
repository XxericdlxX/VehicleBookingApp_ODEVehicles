package cat.copernic.backendProjecte3.repository;

import cat.copernic.backendProjecte3.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositori JPA per als tokens de recuperació de contrasenya.
 * Permet persistir i consultar els tokens temporals del flux de restabliment.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
}
