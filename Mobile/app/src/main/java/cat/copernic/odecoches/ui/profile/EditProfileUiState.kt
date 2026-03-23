package cat.copernic.odecoches.ui.profile

import cat.copernic.odecoches.core.ui.UiText

/**
 * Estat d'interfície de la pantalla d'edició del perfil.
 */
data class EditProfileUiState(
    val isLoading: Boolean = false,
    val fullName: String = "",
    val documentId: String = "",
    val documentExpiry: String = "",
    val documentImageName: String? = null,
    val documentImageBase64: String? = null,
    val driverLicenseType: String = "",
    val driverLicenseExpiry: String = "",
    val driverLicenseImageName: String? = null,
    val driverLicenseImageBase64: String? = null,
    val creditCardNumber: String = "",
    val address: String = "",
    val nationality: String = "",
    val email: String = "",
    val profilePhotoName: String? = null,
    val profilePhotoBase64: String? = null,
    val newPassword: String = "",
    val repeatPassword: String = "",
    val error: UiText? = null,
    val successMessage: UiText? = null
)