package cat.copernic.backendProjecte3.api.auth.dto;

/**
 * Resposta del login.
 *
 * @param token Token de sessió generat pel servidor.
 * @param role Rol de l'usuari autenticat.
 */
public record LoginResponse(String token, String role) {
}
