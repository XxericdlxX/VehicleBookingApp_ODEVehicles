package cat.copernic.odecoches.data.remote.dto

/**
 * Petició per iniciar la recuperació de contrasenya d'un usuari.
 */
data class ForgotPasswordRequest(
    val email: String
)