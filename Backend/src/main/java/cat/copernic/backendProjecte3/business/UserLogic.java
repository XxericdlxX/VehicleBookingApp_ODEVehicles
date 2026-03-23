package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Usuari;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import cat.copernic.backendProjecte3.repository.UsuariRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servei amb la lògica d'autenticació i consulta de rols dels usuaris.
 */
@Service
public class UserLogic {

    @Autowired
    UsuariRepository usuariRepository;

    /**
     * *
     *
     * @param email
     * @param rawPassword
     * @return
     * @throws AccesDenegatException
     */
    public Optional<UserRole> login(String email, String rawPassword) throws AccesDenegatException {

        UserRole ret = UserRole.NONE;

        Usuari user = usuariRepository.findByEmail(email).orElseThrow(() -> new AccesDenegatException("Usuari no existeix"));

        if (PasswordHasher.check(rawPassword, user.getPassword())) {
            ret = user.getRol();
        } else {
            throw new AccesDenegatException("Bad Password");
        }

        return Optional.of(ret);
    }

    /**
     * Consulta el rol associat a un usuari a partir del seu correu.
     *
     * @param email correu electrònic de l'usuari
     * @return rol de l'usuari
     * @throws AccesDenegatException si l'usuari no existeix
     */
    public Optional<UserRole> getRole(String email) throws AccesDenegatException {

        Usuari user = usuariRepository.findByEmail(email).orElseThrow(() -> new AccesDenegatException("Usuari no existeix"));

        return Optional.of(user.getRol());
    }

}
