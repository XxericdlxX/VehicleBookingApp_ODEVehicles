package cat.copernic.odecoches.features.reservations.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.odecoches.features.reservations.data.remote.ReservaCancelResponse
import cat.copernic.odecoches.features.reservations.data.remote.ReservaDetallResponse
import cat.copernic.odecoches.features.reservations.data.repository.ReservaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
/**
 * Estat de la interfície per a la pantalla de detall d'una reserva.
 *
 * Conté tota la informació necessària perquè la UI pugui mostrar:
 * - si les dades s'estan carregant
 * - el detall complet de la reserva
 * - possibles errors durant la càrrega
 *
 * També gestiona l'estat de la cancel·lació de la reserva,
 * indicant si s'està processant, si ha estat correcta o si
 * s'ha produït algun error.
 */
data class ReservationDetailUiState(
    val isLoading: Boolean = false,
    val data: ReservaDetallResponse? = null,
    val error: String? = null,

    val isCancelling: Boolean = false,
    val cancelOk: ReservaCancelResponse? = null,
    val cancelError: String? = null
)
/**
 * ViewModel encarregat de gestionar la pantalla de detall d'una reserva.
 *
 * Aquest ViewModel segueix el patró MVVM i s'encarrega de:
 *
 * - carregar el detall d'una reserva concreta
 * - gestionar l'estat de càrrega de la interfície
 * - permetre anul·lar una reserva si és possible
 * - gestionar els errors de comunicació amb el backend
 *
 * Utilitza el {@link ReservaRepository} per accedir a les dades
 * del backend mitjançant l'API REST.
 *
 * L'estat de la interfície s'exposa mitjançant un {@link StateFlow}
 * perquè la UI de Jetpack Compose es pugui actualitzar
 * automàticament quan canvien les dades.
 *
 * @param repo repositori utilitzat per obtenir i modificar les reserves
 */
class ReservationDetailViewModel(
    private val repo: ReservaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationDetailUiState())
    val uiState: StateFlow<ReservationDetailUiState> = _uiState
    /**
     * Carrega el detall d'una reserva concreta.
     *
     * Realitza una petició al backend per obtenir tota la informació
     * de la reserva i actualitza l'estat de la interfície amb les dades
     * obtingudes o amb un missatge d'error si la petició falla.
     *
     * @param id identificador de la reserva
     * @param userEmail correu electrònic de l'usuari autenticat
     */
    fun load(id: Long, userEmail: String) {
        viewModelScope.launch {
            _uiState.value = ReservationDetailUiState(isLoading = true)
            try {
                val detail = repo.getReservationDetail(id, userEmail)
                _uiState.value = ReservationDetailUiState(data = detail)
            } catch (e: Exception) {
                _uiState.value = ReservationDetailUiState(
                    error = e.message ?: "Error carregant detall"
                )
            }
        }
    }
    /**
     * Anul·la una reserva existent.
     *
     * Envia una petició al backend per cancel·lar la reserva
     * indicada. L'estat de la interfície s'actualitza indicant
     * si la cancel·lació s'ha realitzat correctament o si s'ha
     * produït algun error.
     *
     * @param id identificador de la reserva que es vol anul·lar
     * @param userEmail correu electrònic de l'usuari autenticat
     */
    fun cancel(id: Long, userEmail: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCancelling = true, cancelError = null, cancelOk = null)
            try {
                val resp = repo.cancelReservation(id, userEmail)
                _uiState.value = _uiState.value.copy(isCancelling = false, cancelOk = resp)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCancelling = false,
                    cancelError = e.message ?: "No s'ha pogut anul·lar"
                )
            }
        }
    }
}