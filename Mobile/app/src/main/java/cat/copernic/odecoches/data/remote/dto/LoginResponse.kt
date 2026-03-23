package cat.copernic.odecoches.data.remote.dto

/**
 * Resposta del login.
 *
 * @param token Token de sessió generat pel servidor.
 * @param role Rol de l'usuari autenticat.
 */
/**
 * Resposta del backend amb la informació de la sessió autenticada.
 */
data class LoginResponse(
    val token: String,
    val role: String
)
