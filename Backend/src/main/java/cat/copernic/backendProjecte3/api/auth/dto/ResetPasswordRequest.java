package cat.copernic.backendProjecte3.api.auth.dto;

/**
 * Petició per canviar la contrasenya amb un token temporal.
 *
 * @param token Token temporal de recuperació.
 * @param newPassword Nova contrasenya en text pla.
 */
public record ResetPasswordRequest(String token, String newPassword) {

}
