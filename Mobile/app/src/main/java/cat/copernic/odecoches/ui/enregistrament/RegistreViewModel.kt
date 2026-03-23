package cat.copernic.odecoches.ui.enregistrament

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.odecoches.R
import cat.copernic.odecoches.data.repository.VehicleRepository
import cat.copernic.odecoches.domain.model.RegistreRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel encarregat de gestionar la lògica de la pantalla d'enregistrament d'usuaris.
 * S'encarrega de la validació de dades, la gestió de l'estat de la interfície i la
 * comunicació amb el repositori per completar el registre.
 *
 * @property repository El repositori de vehicles utilitzat per a les operacions de dades.
 */
class RegistreViewModel(private val repository: VehicleRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistreUIEstat())
    /**
     * Flux de dades que representa l'estat actual de la interfície de registre.
     */
    val uiState: StateFlow<RegistreUIEstat> = _uiState.asStateFlow()

    // Expressions regulars per a les validacions de format
    private val DNI_REGEX = Regex("^[0-9]{8}[A-Z]$")
    private val NIE_REGEX = Regex("^[XYZ][0-9]{7}[A-Z]$")
    private val PASSPORT_REGEX = Regex("^[A-Z0-9]{6,9}$")
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}$")
    private val CARD_REGEX = Regex("^[0-9]{16}$")

    // Mínim 6 caràcters, almenys una lletra, un número i un caràcter especial
    private val PASS_REGEX = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$")

    private val _registreExit = MutableSharedFlow<Boolean>()
    /**
     * Flux d'esdeveniments per notificar si el registre s'ha realitzat correctament.
     */
    val registreExit = _registreExit.asSharedFlow()

    // Funcions per actualitzar l'estat de cada camp del formulari
    fun onTipusDocumentChange(it: String) = _uiState.update { s -> s.copy(tipusDocument = it) }
    fun onNomCompletChange(it: String) = _uiState.update { s -> s.copy(nomComplet = it) }
    fun onDniChange(it: String) = _uiState.update { s -> s.copy(numIdentificacio = it) }
    fun onCaducitatDniChange(it: String) = _uiState.update { s -> s.copy(caducitatIdentificacio = it) }
    fun onNacionalitatChange(it: String) = _uiState.update { s -> s.copy(nacionalitat = it) }
    fun onAdrecaChange(it: String) = _uiState.update { s -> s.copy(adreca = it) }
    fun onNumTargetaChange(it: String) = _uiState.update { s -> s.copy(numTargeta = it) }
    fun onEmailChange(it: String) = _uiState.update { s -> s.copy(email = it) }
    fun onPasswordChange(it: String) = _uiState.update { s -> s.copy(password = it) }
    fun onTipusLlicenciaChange(it: String) = _uiState.update { s -> s.copy(tipusLlicencia = it) }
    fun onCaducitatLlicenciaChange(it: String) = _uiState.update { s -> s.copy(caducitatLlicencia = it) }

    /**
     * Actualitza l'estat amb la URI de la imatge del document d'identitat seleccionada.
     */
    fun onImatgeIdentificacioSelected(uri: Uri?) = _uiState.update { it.copy(imatgeIdentificacio = uri) }

    /**
     * Actualitza l'estat amb la URI de la imatge de la llicència de conduir seleccionada.
     */
    fun onImatgeLlicenciaSelected(uri: Uri?) = _uiState.update { it.copy(imatgeLlicencia = uri) }

    /**
     * Executa el procés de registre. Realitza validacions exhaustives de tots els camps
     * i, si són correctes, envia la petició al servidor mitjançant el repositori.
     *
     * @param context El context de l'aplicació necessari per al processament d'imatges.
     */
    fun registrarUsuari(context: android.content.Context) {
        val s = _uiState.value

        // 1. Validació de camps obligatoris (tots excepte les imatges)
        if (s.nomComplet.isBlank() || s.numIdentificacio.isBlank() || s.email.isBlank() ||
            s.password.isBlank() || s.adreca.isBlank() || s.numTargeta.isBlank() ||
            s.nacionalitat.isBlank() || s.tipusLlicencia.isBlank() ||
            s.caducitatLlicencia.isBlank() || s.caducitatIdentificacio.isBlank()) {

            _uiState.update { it.copy(errorMessage = R.string.error_missing_fields) }
            return
        }

        // 2. Validació de format de correu electrònic
        if (!EMAIL_REGEX.matches(s.email)) {
            _uiState.update { it.copy(errorMessage = R.string.error_invalid_email) }
            return
        }

        // 3. Validació de la targeta (format de 16 dígits)
        if (!CARD_REGEX.matches(s.numTargeta)) {
            _uiState.update { it.copy(errorMessage = R.string.error_invalid_card) }
            return
        }

        // 4. Validació de la contrasenya (seguretat mínima requerida)
        if (!PASS_REGEX.matches(s.password)) {
            _uiState.update { it.copy(errorMessage = R.string.error_weak_password) }
            return
        }

        // 5. Validació del format del document d'identitat segons el tipus seleccionat
        val esIdValido = when (s.tipusDocument.uppercase()) {
            "DNI" -> DNI_REGEX.matches(s.numIdentificacio)
            "NIE" -> NIE_REGEX.matches(s.numIdentificacio)
            "PASSPORT", "PASAPORTE" -> PASSPORT_REGEX.matches(s.numIdentificacio)
            else -> s.numIdentificacio.length >= 6
        }

        if (!esIdValido) {
            _uiState.update { it.copy(errorMessage = R.string.error_invalid_id_format) }
            return
        }

        // Si es passen totes les validacions, s'inicia el procés de càrrega i registre
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // Conversió de les imatges seleccionades a format Base64 per a l'enviament
                val dniBase64 = s.imatgeIdentificacio?.let { uri ->
                    cat.copernic.odecoches.core.utils.ImageBase64.uriToBase64Jpeg(context, uri)
                }
                val llicenciaBase64 = s.imatgeLlicencia?.let { uri ->
                    cat.copernic.odecoches.core.utils.ImageBase64.uriToBase64Jpeg(context, uri)
                }

                val request = RegistreRequest(
                    nomComplet = s.nomComplet,
                    dni = s.numIdentificacio,
                    caducitatDni = s.caducitatIdentificacio,
                    nacionalitat = s.nacionalitat,
                    adreca = s.adreca,
                    numTargeta = s.numTargeta,
                    email = s.email,
                    password = s.password,
                    tipusLlicencia = s.tipusLlicencia,
                    caducitatLlicencia = s.caducitatLlicencia,
                    docIdentitatBase64 = dniBase64,
                    docCarnetBase64 = llicenciaBase64
                )

                val response = repository.registrar(request)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = R.string.generic_error) }
                }
            } catch (e: Exception) {
                // Registre d'errors en la consola per a depuració
                println("ERROR REGISTRE: ${e.message}")
                _uiState.update { it.copy(isLoading = false, errorMessage = R.string.generic_error) }
            }
        }
    }
}
