package cat.copernic.odecoches.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.odecoches.R
import cat.copernic.odecoches.core.session.SessionManager
import cat.copernic.odecoches.core.ui.UiText
import cat.copernic.odecoches.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que controla el flux de recuperació i canvi de contrasenya.
 */
class PasswordRecoveryViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PasswordRecoveryUiState(
            email = SessionManager.userEmail.orEmpty()
        )
    )
    val uiState: StateFlow<PasswordRecoveryUiState> = _uiState.asStateFlow()

    /**
     * Actualitza el correu introduït per l'usuari.
     *
     * @param value nou correu electrònic
     */
    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            infoMessage = null,
            errorMessage = null
        )
    }

    /**
     * Actualitza el token introduït al formulari.
     *
     * @param value nou valor del token
     */
    fun onTokenChange(value: String) {
        _uiState.value = _uiState.value.copy(
            token = value,
            infoMessage = null,
            errorMessage = null
        )
    }

    /**
     * Actualitza la nova contrasenya del formulari.
     *
     * @param value nova contrasenya
     */
    fun onNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            newPassword = value,
            infoMessage = null,
            errorMessage = null
        )
    }

    /**
     * Actualitza el camp de repetició de contrasenya.
     *
     * @param value confirmació de la nova contrasenya
     */
    fun onRepeatPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            repeatPassword = value,
            infoMessage = null,
            errorMessage = null
        )
    }

    /**
     * Reinicia l'estat del formulari mantenint el correu introduït.
     */
    fun resetForEntry() {
        val emailSessio = SessionManager.userEmail.orEmpty()
        _uiState.value = PasswordRecoveryUiState(
            email = emailSessio,
            step = RecoveryStep.REQUEST_TOKEN
        )
    }

    /**
     * Sol·licita al backend l'enviament d'un token de recuperació.
     */
    fun requestToken() {
        val emailFromState = _uiState.value.email.trim()
        val emailFromSession = SessionManager.userEmail?.trim().orEmpty()
        val email = emailFromState.ifBlank { emailFromSession }

        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = UiText.StringResource(R.string.validation_required_email)
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            email = email,
            isLoading = true,
            infoMessage = null,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                val code = repository.forgotPassword(email)

                when (code) {
                    in 200..299 -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            step = RecoveryStep.RESET_PASSWORD,
                            infoMessage = UiText.StringResource(R.string.password_reset_token_sent)
                        )
                    }

                    404 -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = UiText.StringResource(R.string.password_reset_email_not_found)
                        )
                    }

                    else -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = UiText.StringResource(R.string.password_reset_send_error)
                        )
                    }
                }
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = UiText.StringResource(R.string.password_reset_send_error)
                )
            }
        }
    }

    /**
     * Envia al backend la petició per restablir la contrasenya.
     */
    fun resetPassword() {
        val token = _uiState.value.token.trim()
        val p1 = _uiState.value.newPassword
        val p2 = _uiState.value.repeatPassword
        val email = _uiState.value.email

        if (token.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = UiText.StringResource(R.string.validation_required_token)
            )
            return
        }

        if (p1.isBlank() || p2.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = UiText.StringResource(R.string.validation_required_new_password_twice)
            )
            return
        }

        if (p1 != p2) {
            _uiState.value = _uiState.value.copy(
                errorMessage = UiText.StringResource(R.string.validation_passwords_do_not_match)
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                infoMessage = null,
                errorMessage = null
            )

            try {
                val ok = repository.resetPassword(token, p1)

                _uiState.value = if (ok) {
                    PasswordRecoveryUiState(
                        email = email,
                        step = RecoveryStep.REQUEST_TOKEN,
                        infoMessage = UiText.StringResource(R.string.password_reset_success)
                    )
                } else {
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = UiText.StringResource(R.string.password_reset_invalid_token)
                    )
                }
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = UiText.StringResource(R.string.password_reset_change_error)
                )
            }
        }
    }
}