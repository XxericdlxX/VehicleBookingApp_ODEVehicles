package cat.copernic.backendProjecte3.api.auth.dto;

/**
 * Dades d'entrada per iniciar sessió.
 *
 * @param email Email de l'usuari.
 * @param password Contrasenya en text pla.
 */
public record LoginRequest(String email, String password) {

}
