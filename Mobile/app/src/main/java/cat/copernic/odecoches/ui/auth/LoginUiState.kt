package cat.copernic.odecoches.ui.auth

/**
 * Estat d'interfície de la pantalla de login.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val generalError: Int? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
)