package cat.copernic.odecoches.ui.auth

import cat.copernic.odecoches.core.ui.UiText

/**
 * Estat d'interfície del flux de recuperació de contrasenya.
 */
data class PasswordRecoveryUiState(
    val email: String = "",
    val token: String = "",
    val newPassword: String = "",
    val repeatPassword: String = "",
    val isLoading: Boolean = false,
    val step: RecoveryStep = RecoveryStep.REQUEST_TOKEN,
    val infoMessage: UiText? = null,
    val errorMessage: UiText? = null
)

/**
 * Passos disponibles dins del flux de recuperació de contrasenya.
 */
enum class RecoveryStep {
    REQUEST_TOKEN,
    RESET_PASSWORD
}