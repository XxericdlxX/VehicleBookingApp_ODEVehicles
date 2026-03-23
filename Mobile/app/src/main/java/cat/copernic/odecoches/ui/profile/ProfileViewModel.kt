package cat.copernic.odecoches.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.odecoches.R
import cat.copernic.odecoches.core.session.SessionManager
import cat.copernic.odecoches.core.ui.UiText
import cat.copernic.odecoches.data.repository.AuthRepository
import cat.copernic.odecoches.data.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que carrega el perfil del client i gestiona el tancament de sessió.
 */
class ProfileViewModel : ViewModel() {

    private val repo = ClientRepository()
    private val authRepo = AuthRepository()

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

        /**
     * Carrega des del backend les dades del perfil del client autenticat.
     */
    fun loadProfile() {
        viewModelScope.launch {
            val token = SessionManager.token

            if (token.isNullOrBlank()) {
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    error = UiText.StringResource(R.string.error_not_logged_in),
                    profile = null
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val response = repo.getProfile("Bearer $token")

                when {
                    response.isSuccessful -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            profile = response.body(),
                            error = null
                        )
                    }

                    response.code() == 401 -> {
                        SessionManager.clear()
                        _uiState.value = ProfileUiState(
                            isLoading = false,
                            error = UiText.StringResource(R.string.error_not_logged_in),
                            profile = null
                        )
                    }

                    else -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = UiText.StringResource(
                                R.string.error_loading_profile_with_code,
                                listOf(response.code())
                            ),
                            profile = null
                        )
                    }
                }
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = UiText.StringResource(R.string.error_connection),
                    profile = null
                )
            }
        }
    }

        /**
     * Tanca la sessió al backend i neteja la sessió local de l'aplicació.
     *
     * @param onFinished acció a executar quan el procés de logout finalitza
     */
    fun logout(onFinished: () -> Unit) {
        viewModelScope.launch {
            val token = SessionManager.token

            try {
                if (!token.isNullOrBlank()) {
                    authRepo.logout("Bearer $token")
                }
            } catch (_: Exception) {
            } finally {
                SessionManager.clear()
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    error = null,
                    profile = null
                )
                onFinished()
            }
        }
    }
}