package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.api.common.ApiException;
import cat.copernic.backendProjecte3.entities.AuthToken;
import cat.copernic.backendProjecte3.repository.AuthTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servei encarregat de crear, validar i revocar els tokens de sessió.
 */
@Service
public class AuthTokenService {

    private static final String PREFIX_TOKEN = "Token ";
    private static final String PREFIX_BEARER = "Bearer ";
    private static final int SESSION_HOURS = 24;

    private final AuthTokenRepository repo;

    /**
     * Crea el servei de tokens amb el repositori indicat.
     *
     * @param repo repositori de persistència dels tokens de sessió
     */
    public AuthTokenService(AuthTokenRepository repo) {
        this.repo = repo;
    }

    /**
     * Crea un token nou quan l'usuari fa login correctament
     */
    /**
     * Genera una nova sessió per a l'usuari indicat.
     *
     * @param email correu electrònic de l'usuari autenticat
     * @return token de sessió generat
     */
    public String createSession(String email) {
        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(SESSION_HOURS);
        repo.save(new AuthToken(token, email, expiresAt));
        return token;
    }

    /**
     * Valida el token i retorna l'email associat. Header permès: -
     * Authorization: Token <token>
     * - Authorization: Bearer <token>
     */
    /**
     * Valida el token rebut i en retorna el correu associat.
     *
     * @param authHeader capçalera Authorization de la petició
     * @return correu electrònic vinculat al token
     */
    public String requireEmail(String authHeader) {
        String token = extractToken(authHeader);

        AuthToken stored = repo.findById(token)
                .orElseThrow(() -> new ApiException(401, "Token invalid"));

        if (stored.isRevoked()) {
            throw new ApiException(401, "Sessio tancada (token revocat)");
        }

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(401, "Sessio caducada");
        }

        return stored.getEmail();
    }

    /**
     * Logout: revoca el token del header Authorization.
     */
    /**
     * Revoca el token de sessió rebut a la capçalera Authorization.
     *
     * @param authHeader capçalera Authorization de la petició
     */
    public void revoke(String authHeader) {
        String token = extractToken(authHeader);

        AuthToken stored = repo.findById(token)
                .orElseThrow(() -> new ApiException(401, "Token invalid"));

        stored.setRevoked(true);
        repo.save(stored);
    }

    /**
     * Logout de tots els dispositius: revoca tots els tokens actius d'un email.
     *
     * Revoca tots els tokens actius de l'usuari indicat.
     *
     * @param email correu electrònic de l'usuari
     * @return nombre de sessions revocades
     */
    public int revokeAllForEmail(String email) {
        List<AuthToken> tokens = repo.findByEmailAndRevokedFalse(email);
        tokens.forEach(t -> t.setRevoked(true));
        repo.saveAll(tokens);
        return tokens.size();
    }

    /**
     * Extreu el valor del token a partir de la capçalera Authorization.
     *
     * @param header capçalera rebuda a la petició
     * @return valor del token sense prefixos
     */
    private String extractToken(String header) {
        if (header == null || header.isBlank()) {
            throw new ApiException(401, "Falta el header Authorization");
        }

        String token;
        if (header.startsWith(PREFIX_TOKEN)) {
            token = header.substring(PREFIX_TOKEN.length()).trim();
        } else if (header.startsWith(PREFIX_BEARER)) {
            token = header.substring(PREFIX_BEARER.length()).trim();
        } else {
            throw new ApiException(401, "Format incorrecte. Usa: Authorization: Token <token> (o Bearer <token>)");
        }

        if (token.isBlank()) {
            throw new ApiException(401, "Token buit. Usa: Authorization: Token <token>");
        }

        return token;
    }
}
