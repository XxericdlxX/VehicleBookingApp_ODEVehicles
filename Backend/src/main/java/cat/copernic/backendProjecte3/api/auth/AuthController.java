package cat.copernic.backendProjecte3.api.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cat.copernic.backendProjecte3.api.auth.dto.ForgotPasswordRequest;
import cat.copernic.backendProjecte3.api.auth.dto.ForgotPasswordResponse;
import cat.copernic.backendProjecte3.api.auth.dto.LoginRequest;
import cat.copernic.backendProjecte3.api.auth.dto.LoginResponse;
import cat.copernic.backendProjecte3.api.auth.dto.ResetPasswordRequest;
import cat.copernic.backendProjecte3.api.common.ApiException;
import cat.copernic.backendProjecte3.business.AuthTokenService;
import cat.copernic.backendProjecte3.business.PasswordResetService;
import cat.copernic.backendProjecte3.business.UserLogic;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
/**
 * Controlador REST responsable dels fluxos d'autenticació de l'aplicació.
 * <p>
 * Gestiona les operacions de login, logout, recuperació de contrasenya i
 * restabliment de contrasenya.
 * </p>
 */
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserLogic userLogic;
    private final AuthTokenService tokenService;
    private final PasswordResetService resetService;

    /**
     * Crea el controlador d'autenticació amb els serveis necessaris.
     *
     * @param userLogic lògica d'autenticació i consulta de rols
     * @param tokenService servei de gestió de tokens de sessió
     * @param resetService servei de recuperació de contrasenya
     */
    public AuthController(UserLogic userLogic, AuthTokenService tokenService, PasswordResetService resetService) {
        this.userLogic = userLogic;
        this.tokenService = tokenService;
        this.resetService = resetService;
    }

    @PostMapping("/login")
    /**
     * Autentica un usuari amb el correu i la contrasenya indicats.
     *
     * @param req dades del login
     * @return resposta amb el token de sessió i el rol de l'usuari
     */
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        log.info("POST request received in AuthController");
        if (req == null || req.email() == null || req.email().isBlank()
                || req.password() == null || req.password().isBlank()) {
            throw new ApiException(400, "Email i contrasenya són obligatoris");
        }

        Optional<UserRole> roleOpt;
        try {
            roleOpt = userLogic.login(req.email(), req.password());
        } catch (AccesDenegatException e) {
            throw new ApiException(401, "Credencials incorrectes");
        }

        if (roleOpt.isEmpty() || roleOpt.get() == UserRole.NONE) {
            throw new ApiException(401, "Usuari sense permisos");
        }

        String token = tokenService.createSession(req.email());
        return ResponseEntity.ok(new LoginResponse(token, roleOpt.get().name()));
    }

    @PostMapping("/logout")
    /**
     * Tanca la sessió associada al token rebut a la capçalera Authorization.
     *
     * @param auth capçalera d'autenticació amb el token de sessió
     * @return resposta sense contingut quan el logout es completa correctament
     */
    public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = false) String auth) {
        log.info("POST request received in AuthController");
        tokenService.revoke(auth);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/forgot")
    /**
     * Inicia el procés de recuperació de contrasenya per a un usuari.
     *
     * @param req petició amb el correu electrònic de l'usuari
     * @return missatge informatiu sobre l'enviament del token
     */
    public ResponseEntity<ForgotPasswordResponse> forgot(@RequestBody ForgotPasswordRequest req) {
        log.info("POST request received in AuthController");
        if (req == null || req.email() == null || req.email().isBlank()) {
            throw new ApiException(400, "Email obligatori");
        }

        resetService.createResetToken(req.email());

        return ResponseEntity.ok(new ForgotPasswordResponse(
                "Si el correu existeix, t'hem enviat un missatge amb el token de recuperació."
        ));
    }

    @PostMapping("/password/reset")
    /**
     * Restableix la contrasenya a partir d'un token temporal.
     *
     * @param req petició amb el token i la nova contrasenya
     * @return resposta sense contingut quan la contrasenya s'actualitza
     */
    public ResponseEntity<Void> reset(@RequestBody ResetPasswordRequest req) {
        log.info("POST request received in AuthController");
        if (req == null || req.token() == null || req.token().isBlank()
                || req.newPassword() == null || req.newPassword().isBlank()) {
            throw new ApiException(400, "Token i nova contrasenya són obligatoris");
        }

        resetService.resetPassword(req.token(), req.newPassword());
        return ResponseEntity.noContent().build();
    }
}
