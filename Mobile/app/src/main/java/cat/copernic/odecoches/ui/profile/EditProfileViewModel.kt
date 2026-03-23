package cat.copernic.odecoches.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.odecoches.R
import cat.copernic.odecoches.core.session.SessionManager
import cat.copernic.odecoches.core.ui.UiText
import cat.copernic.odecoches.data.remote.dto.ClientProfileUpdateRequest
import cat.copernic.odecoches.data.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que carrega, edita i desa les dades del perfil del client.
 */
class EditProfileViewModel : ViewModel() {

    private val repo = ClientRepository()

    private val _uiState = MutableStateFlow(EditProfileUiState(isLoading = true))
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    companion object {
        val LICENSE_TYPES = listOf("AM", "A1", "A2", "A", "B", "B+E", "C")

        private val DNI_REGEX = Regex("^[0-9]{8}[A-Z]$")
        private val NIE_REGEX = Regex("^[XYZ][0-9]{7}[A-Z]$")
        private val PASSPORT_REGEX = Regex("^[A-Z0-9]{6,9}$")
        private val CARD_REGEX = Regex("^[0-9]{16}$")
    }

        /**
     * Aplica una transformació sobre l'estat actual de la pantalla.
     *
     * @param reducer funció que rep l'estat actual i en retorna un de nou
     */
    fun update(reducer: (EditProfileUiState) -> EditProfileUiState) {
        _uiState.value = reducer(_uiState.value)
    }

        /**
     * Carrega des del backend les dades del perfil autenticat.
     */
    fun loadFromServer() {
        viewModelScope.launch {
            val t = SessionManager.token
            if (t.isNullOrBlank()) {
                _uiState.value = EditProfileUiState(
                    isLoading = false,
                    error = UiText.StringResource(R.string.error_not_logged_in)
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                successMessage = null
            )

            try {
                var resp = repo.getProfile("Bearer $t")
                if (resp.code() == 401) resp = repo.getProfile("Token $t")

                if (resp.isSuccessful) {
                    val p = resp.body()
                    if (p != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            fullName = p.nomComplet.orEmpty(),
                            email = p.username,
                            documentId = p.dni.uppercase(),
                            documentExpiry = p.dataCaducitatDocument.orEmpty(),
                            address = p.adreca.orEmpty(),
                            nationality = p.nacionalitat.orEmpty(),
                            driverLicenseType = p.carnetConduir.orEmpty(),
                            driverLicenseExpiry = p.dataCaducitatCarnetConduir.orEmpty(),
                            creditCardNumber = p.numeroTargetaCredit.orEmpty().filter { it.isDigit() }.take(16),
                            profilePhotoBase64 = p.fotoPerfilBase64,
                            documentImageBase64 = p.docIdentitatBase64,
                            driverLicenseImageBase64 = p.docCarnetBase64,
                            profilePhotoName = if (p.fotoPerfilBase64.isNullOrBlank()) null else "foto_guardada.jpg",
                            documentImageName = if (p.docIdentitatBase64.isNullOrBlank()) null else "dni_guardat.jpg",
                            driverLicenseImageName = if (p.docCarnetBase64.isNullOrBlank()) null else "carnet_guardat.jpg",
                            error = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = UiText.StringResource(R.string.error_empty_server_response)
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = UiText.StringResource(
                            R.string.error_loading_with_code,
                            listOf(resp.code())
                        )
                    )
                }
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = UiText.StringResource(R.string.error_connection)
                )
            }
        }
    }

        /**
     * Desa al backend els canvis fets al perfil.
     *
     * @param onSuccess acció a executar quan el desat finalitza correctament
     */
    fun saveToServer(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val t = SessionManager.token
            if (t.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(
                    error = UiText.StringResource(R.string.error_not_logged_in),
                    successMessage = null
                )
                return@launch
            }

            val s = _uiState.value
            val normalizedDocumentId = s.documentId.trim().uppercase()
            val normalizedCreditCard = s.creditCardNumber.filter { it.isDigit() }

            if (normalizedDocumentId.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    error = UiText.StringResource(R.string.validation_document_required),
                    successMessage = null
                )
                return@launch
            }

            val isValidDocument =
                DNI_REGEX.matches(normalizedDocumentId) ||
                        NIE_REGEX.matches(normalizedDocumentId) ||
                        PASSPORT_REGEX.matches(normalizedDocumentId)

            if (!isValidDocument) {
                _uiState.value = _uiState.value.copy(
                    error = UiText.StringResource(R.string.error_invalid_id_format),
                    successMessage = null
                )
                return@launch
            }

            if (normalizedCreditCard.isNotBlank() && !CARD_REGEX.matches(normalizedCreditCard)) {
                _uiState.value = _uiState.value.copy(
                    error = UiText.StringResource(R.string.error_invalid_card),
                    successMessage = null
                )
                return@launch
            }

            if (s.driverLicenseType.isNotBlank() && s.driverLicenseType !in LICENSE_TYPES) {
                _uiState.value = _uiState.value.copy(
                    error = UiText.StringResource(R.string.validation_invalid_license_type),
                    successMessage = null
                )
                return@launch
            }

            if (s.newPassword.isNotBlank() || s.repeatPassword.isNotBlank()) {
                _uiState.value = _uiState.value.copy(
                    error = UiText.StringResource(R.string.error_password_change_not_available_here),
                    successMessage = null
                )
                return@launch
            }

                            /**
                 * Converteix una cadena buida en {@code null} abans d'enviar-la al backend.
                 *
                 * @param v valor original del camp
                 * @return valor retallat o {@code null} si és buit
                 */
                fun blankToNull(v: String) = v.trim().ifBlank { null }

            val req = ClientProfileUpdateRequest(
                nomComplet = blankToNull(s.fullName),
                dni = normalizedDocumentId,
                dataCaducitatDocument = blankToNull(s.documentExpiry),
                adreca = blankToNull(s.address),
                nacionalitat = blankToNull(s.nationality),
                carnetConduir = blankToNull(s.driverLicenseType),
                dataCaducitatCarnetConduir = blankToNull(s.driverLicenseExpiry),
                numeroTargetaCredit = normalizedCreditCard.ifBlank { null },
                fotoPerfilBase64 = s.profilePhotoBase64?.takeIf { it.isNotBlank() },
                docIdentitatBase64 = s.documentImageBase64?.takeIf { it.isNotBlank() },
                docCarnetBase64 = s.driverLicenseImageBase64?.takeIf { it.isNotBlank() }
            )

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                successMessage = null
            )

            try {
                var resp = repo.updateProfile("Bearer $t", req)
                if (resp.code() == 401) resp = repo.updateProfile("Token $t", req)

                if (resp.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = UiText.StringResource(R.string.profile_changes_saved),
                        error = null
                    )
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = UiText.StringResource(
                            R.string.error_saving_with_code,
                            listOf(resp.code())
                        )
                    )
                }
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = UiText.StringResource(R.string.error_connection)
                )
            }
        }
    }
}
