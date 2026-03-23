package cat.copernic.odecoches.data.remote.dto

/**
 * Petició de login.
 *
 * @param email Correu electrònic de l'usuari.
 * @param password Contrasenya en text pla.
 */
/**
 * Petició amb les credencials necessàries per iniciar sessió.
 */
data class LoginRequest(
    val email: String,
    val password: String
)
