package cat.copernic.odecoches.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.odecoches.R
import cat.copernic.odecoches.core.session.SessionManager
import cat.copernic.odecoches.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona l'estat i les accions de la pantalla de login.
 */
class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Actualitza el correu introduït a la pantalla.
     *
     * @param email nou valor del camp de correu
     */
    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null,
            generalError = null
        )
    }

    /**
     * Actualitza la contrasenya introduïda a la pantalla.
     *
     * @param password nou valor del camp de contrasenya
     */
    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null,
            generalError = null
        )
    }

    /**
     * Valida les dades del formulari i inicia el procés de login.
     */
    fun login() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        var emailError: Int? = null
        var passwordError: Int? = null

        if (email.isBlank()) {
            emailError = R.string.validation_required_email
        }

        if (password.isBlank()) {
            passwordError = R.string.validation_required_password
        }

        if (emailError != null || passwordError != null) {
            _uiState.value = _uiState.value.copy(
                emailError = emailError,
                passwordError = passwordError,
                generalError = null
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            emailError = null,
            passwordError = null,
            generalError = null
        )

        viewModelScope.launch {
            try {
                val response = authRepository.login(email, password)

                if (response != null && response.token.isNotBlank()) {
                    sessionManager.saveSession(
                        token = response.token,
                        email = email
                    )

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        generalError = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        generalError = R.string.error_login_generic
                    )
                }
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = false,
                    generalError = R.string.error_login_generic
                )
            }
        }
    }

    /**
     * Reinicia l'estat visual del formulari de login.
     */
    fun reset() {
        _uiState.value = LoginUiState()
    }
}