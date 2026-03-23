package cat.copernic.backendProjecte3.api.auth.dto;

/**
 * Resposta de recuperació de contrasenya.
 *
 * @param message Missatge informatiu (NO inclou el token).
 */
public record ForgotPasswordResponse(String message) {
}
