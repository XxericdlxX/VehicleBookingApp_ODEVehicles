package cat.copernic.odecoches.ui.profile

import cat.copernic.odecoches.core.ui.UiText
import cat.copernic.odecoches.data.remote.dto.ClientProfileResponse

/**
 * Estat d'interfície de la pantalla de consulta del perfil.
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val profile: ClientProfileResponse? = null
)