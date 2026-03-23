package cat.copernic.odecoches.features.reservations.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.odecoches.features.reservations.data.remote.ReservaResponse
import cat.copernic.odecoches.features.reservations.data.repository.ReservaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
/**
 * Estat de la interfície per a la pantalla de llistat de reserves.
 *
 * Conté la informació necessària perquè la UI mostri:
 * - si les dades s'estan carregant
 * - la llista de reserves de l'usuari
 * - possibles errors durant la càrrega
 * - l'ordre actual de visualització de les reserves
 */
data class MyReservationsUiState(
    val isLoading: Boolean = false,
    val items: List<ReservaResponse> = emptyList(),
    val error: String? = null,
    val order: String = "desc"
)
/**
 * ViewModel encarregat de gestionar la pantalla de llistat de reserves
 * de l'usuari autenticat.
 *
 * Aquest ViewModel forma part de l'arquitectura MVVM i s'encarrega de:
 *
 * - carregar les reserves de l'usuari des del backend
 * - gestionar l'estat de càrrega de la interfície
 * - controlar l'ordre de visualització de les reserves
 * - gestionar errors de comunicació amb el servidor
 *
 * Les dades es recuperen a través del {@link ReservaRepository}.
 *
 * L'estat de la UI s'exposa mitjançant un {@link StateFlow}
 * perquè Jetpack Compose pugui actualitzar la interfície
 * automàticament quan canvien les dades.
 *
 * @param repo repositori utilitzat per obtenir les reserves del backend
 */
class MyReservationsViewModel(
    private val repo: ReservaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyReservationsUiState())
    val uiState: StateFlow<MyReservationsUiState> = _uiState
    /**
     * Canvia l'ordre de classificació de les reserves.
     *
     * Si l'ordre és diferent de l'actual, es torna a carregar
     * la llista de reserves amb el nou criteri d'ordenació.
     *
     * @param userEmail correu electrònic de l'usuari autenticat
     * @param order ordre de classificació ("asc" o "desc")
     */
    fun setOrder(userEmail: String, order: String) {
        if (_uiState.value.order == order) return
        load(userEmail, order)
    }
    /**
     * Carrega les reserves de l'usuari des del backend.
     *
     * Actualitza l'estat de la UI indicant que s'estan carregant
     * les dades i posteriorment actualitza la llista de reserves
     * o el missatge d'error en cas que la petició falli.
     *
     * @param userEmail correu electrònic de l'usuari autenticat
     * @param order ordre de classificació de les reserves
     */
    fun load(userEmail: String, order: String = _uiState.value.order) {
        viewModelScope.launch {
            // Manté items i order, només posa loading
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                order = order
            )

            try {
                val data = repo.getMyReservations(userEmail, order = order) // <-- ara sí
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    items = data
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error carregant reserves"
                )
            }
        }
    }
}

