package cat.copernic.odecoches.data.remote.dto

/**
 * Petició per restablir la contrasenya amb un token temporal.
 */
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)