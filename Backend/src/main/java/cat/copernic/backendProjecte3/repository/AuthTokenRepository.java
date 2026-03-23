package cat.copernic.backendProjecte3.repository;

import cat.copernic.backendProjecte3.entities.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositori JPA dels tokens d'autenticació.
 */
@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {

    /**
     * Recupera els tokens actius d'un usuari que encara no han estat revocats.
     *
     * @param email correu electrònic de l'usuari
     * @return llista de tokens actius
     */
    List<AuthToken> findByEmailAndRevokedFalse(String email);
}
