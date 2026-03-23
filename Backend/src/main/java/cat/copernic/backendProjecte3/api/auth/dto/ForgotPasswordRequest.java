package cat.copernic.backendProjecte3.api.auth.dto;

/**
 * Petició per iniciar la recuperació de contrasenya.
 *
 * @param email Email de l'usuari.
 */
public record ForgotPasswordRequest(String email) {

}
