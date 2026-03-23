package cat.copernic.odecoches.features.reservations.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.odecoches.domain.model.Vehicle
import cat.copernic.odecoches.features.reservations.data.remote.ReservaCreateRequest
import cat.copernic.odecoches.features.reservations.data.remote.ReservaResponse
import cat.copernic.odecoches.features.reservations.data.repository.ReservaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
/**
 * Estat de la interfície del flux de creació de reserves.
 *
 * Aquest estat conté tota la informació necessària per gestionar
 * el procés de reserva dins de la capa de presentació.
 *
 * Inclou:
 * - estat de càrrega
 * - missatges d'error
 * - dates seleccionades
 * - vehicles disponibles
 * - vehicle seleccionat
 * - informació de previsualització de la reserva
 * - identificador de la reserva creada
 */
data class ReservaFlowUiState(
    val loading: Boolean = false,
    val error: String? = null,

    val dataIniciDate: LocalDate? = null,
    val dataFiDate: LocalDate? = null,

    val dataIniciText: String = "",
    val dataFiText: String = "",

    val disponibles: List<Vehicle> = emptyList(),
    val vehicleSeleccionat: Vehicle? = null,

    val preview: ReservaResponse? = null,
    val reservaCreadaId: Long? = null
)
/**
 * ViewModel encarregat de gestionar el flux complet de creació d'una reserva.
 *
 * Aquest ViewModel forma part de l'arquitectura MVVM i s'encarrega de:
 *
 * - gestionar l'estat de la pantalla de reserva
 * - validar les dates introduïdes per l'usuari
 * - consultar els vehicles disponibles
 * - generar una previsualització de la reserva
 * - confirmar la creació de la reserva
 *
 * Utilitza el repositori {@link ReservaRepository} per comunicar-se
 * amb el backend mitjançant l'API REST.
 *
 * L'estat de la interfície s'exposa mitjançant un {@link StateFlow}
 * perquè la UI de Jetpack Compose pugui reaccionar automàticament
 * als canvis d'estat.
 *
 * @param repo repositori que gestiona les operacions de reserva
 */
@RequiresApi(Build.VERSION_CODES.O)
private val ISO = DateTimeFormatter.ISO_LOCAL_DATE

class ReservaFlowViewModel(
    private val repo: ReservaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReservaFlowUiState())
    val state: StateFlow<ReservaFlowUiState> = _state
    /**
     * Actualitza les dates introduïdes per l'usuari.
     *
     * Converteix els textos introduïts a objectes LocalDate si el format
     * és correcte i actualitza l'estat del ViewModel.
     *
     * @param iniciText text de la data d'inici introduït per l'usuari
     * @param fiText text de la data de finalització introduït per l'usuari
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setDates(iniciText: String, fiText: String) {
        val iniciParsed = parseToLocalDateOrNull(iniciText)
        val fiParsed = parseToLocalDateOrNull(fiText)

        _state.value = _state.value.copy(
            dataIniciText = iniciText,
            dataFiText = fiText,
            dataIniciDate = iniciParsed,
            dataFiDate = fiParsed,
            error = null
        )
    }
    /**
     * Consulta els vehicles disponibles per al rang de dates seleccionat.
     *
     * Realitza una validació prèvia de les dates introduïdes i després
     * crida el repositori per obtenir la llista de vehicles disponibles.
     *
     * En cas d'error es gestiona el missatge corresponent a l'estat de la UI.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun buscarDisponibles() {
        val inici = _state.value.dataIniciDate
        val fi = _state.value.dataFiDate

        // Validación front
        if (inici == null || fi == null) {
            _state.value = _state.value.copy(error = "Dates no vàlides. Format: YYYY-MM-DD")
            return
        }
        if (fi.isBefore(inici)) {
            _state.value = _state.value.copy(error = "La data fi no pot ser abans de la data inici")
            return
        }

        val iniciIso = inici.format(ISO)
        val fiIso = fi.format(ISO)

        viewModelScope.launch {
            // Reseteo para evitar datos antiguos
            _state.value = _state.value.copy(
                loading = true,
                error = null,
                disponibles = emptyList(),
                vehicleSeleccionat = null,
                preview = null,
                reservaCreadaId = null
            )
            try {
                val list = repo.vehiclesDisponibles(iniciIso, fiIso)
                _state.value = _state.value.copy(loading = false, disponibles = list)
            } catch (e: HttpException) {
                _state.value = _state.value.copy(loading = false, error = httpMessage(e.code()))
            } catch (e: IOException) {
                _state.value = _state.value.copy(loading = false, error = "No hi ha connexió amb el servidor.")
            } catch (_: Exception) {
                _state.value = _state.value.copy(loading = false, error = "S'ha produït un error inesperat.")
            }
        }
    }
    /**
     * Estableix el vehicle seleccionat per l'usuari.
     *
     * Quan es selecciona un vehicle es reinicia la previsualització
     * de la reserva per evitar informació antiga.
     *
     * @param vehicle vehicle seleccionat per l'usuari
     */
    fun seleccionarVehicle(vehicle: Vehicle) {
        _state.value = _state.value.copy(
            vehicleSeleccionat = vehicle,
            preview = null,
            reservaCreadaId = null,
            error = null
        )
    }
    /**
     * Genera una previsualització de la reserva.
     *
     * Aquesta operació calcula l'import total i la fiança de la reserva
     * abans de confirmar-la definitivament.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun carregarPreview() {
        val v = _state.value.vehicleSeleccionat ?: return
        val inici = _state.value.dataIniciDate
        val fi = _state.value.dataFiDate

        if (inici == null || fi == null) {
            _state.value = _state.value.copy(error = "Dates no vàlides. Format: YYYY-MM-DD")
            return
        }
        if (fi.isBefore(inici)) {
            _state.value = _state.value.copy(error = "La data fi no pot ser abans de la data inici")
            return
        }

        val iniciIso = inici.format(ISO)
        val fiIso = fi.format(ISO)

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null, preview = null, reservaCreadaId = null)
            try {
                val prev = repo.previewReserva(ReservaCreateRequest(v.matricula, iniciIso, fiIso))
                _state.value = _state.value.copy(loading = false, preview = prev)
            } catch (e: HttpException) {
                _state.value = _state.value.copy(loading = false, error = httpMessage(e.code()))
            } catch (e: IOException) {
                _state.value = _state.value.copy(loading = false, error = "No hi ha connexió amb el servidor.")
            } catch (_: Exception) {
                _state.value = _state.value.copy(loading = false, error = "S'ha produït un error inesperat.")
            }
        }
    }
    /**
     * Confirma la creació d'una nova reserva.
     *
     * Valida les dades introduïdes i envia la petició al backend
     * per crear la reserva associada a l'usuari autenticat.
     *
     * @param userEmail correu electrònic de l'usuari que crea la reserva
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmarReserva(userEmail: String) {
        val v = _state.value.vehicleSeleccionat ?: run {
            _state.value = _state.value.copy(error = "Selecciona un vehicle abans de confirmar.")
            return
        }

        val inici = _state.value.dataIniciDate
        val fi = _state.value.dataFiDate

        if (userEmail.isBlank()) {
            _state.value = _state.value.copy(error = "Has d'iniciar sessió per confirmar la reserva.")
            return
        }

        if (inici == null || fi == null) {
            _state.value = _state.value.copy(error = "Dates no vàlides. Format: YYYY-MM-DD")
            return
        }
        if (fi.isBefore(inici)) {
            _state.value = _state.value.copy(error = "La data fi no pot ser abans de la data inici")
            return
        }

        val iniciIso = inici.format(ISO)
        val fiIso = fi.format(ISO)

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val res = repo.crearReserva(
                    userEmail,
                    ReservaCreateRequest(v.matricula, iniciIso, fiIso)
                )
                _state.value = _state.value.copy(
                    loading = false,
                    reservaCreadaId = res.idReserva
                )
            } catch (e: HttpException) {
                _state.value = _state.value.copy(loading = false, error = httpMessage(e.code()))
            } catch (e: IOException) {
                _state.value = _state.value.copy(loading = false, error = "No hi ha connexió amb el servidor.")
            } catch (_: Exception) {
                _state.value = _state.value.copy(loading = false, error = "S'ha produït un error inesperat.")
            }
        }
    }
    /**
     * Elimina el missatge d'error actual de l'estat de la UI.
     */
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
    /**
     * Reinicia completament l'estat del flux de reserva.
     *
     * S'utilitza després de crear una reserva correctament
     * o quan es vol començar el procés des de zero.
     */
    fun resetFlow() {
        _state.value = ReservaFlowUiState()
    }
    private fun httpMessage(code: Int): String = when (code) {
        400 -> "Dades incorrectes. Revisa les dates."
        401 -> "Has d'iniciar sessió per continuar."
        403 -> "No tens permís per fer aquesta acció."
        404 -> "No s'ha trobat el recurs."
        409 -> "El vehicle no està disponible en aquestes dates."
        500 -> "Error intern del servidor. Torna-ho a provar més tard."
        else -> "Error del servidor ($code)."
    }
    /**
     * Intenta convertir un text a un objecte LocalDate.
     *
     * Accepta dos formats:
     * - YYYY-MM-DD
     * - DD-MM-YYYY
     *
     * @param input text introduït per l'usuari
     * @return data convertida o null si el format no és vàlid
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseToLocalDateOrNull(input: String): LocalDate? {
        val t = input.trim()
        if (t.isEmpty()) return null

        val normalized = t.replace('/', '-')

        return try {
            when {
                Regex("""\d{4}-\d{2}-\d{2}""").matches(normalized) ->
                    LocalDate.parse(normalized, ISO)

                Regex("""\d{2}-\d{2}-\d{4}""").matches(normalized) -> {
                    val parts = normalized.split("-")
                    val dd = parts[0].toInt()
                    val mm = parts[1].toInt()
                    val yyyy = parts[2].toInt()
                    LocalDate.of(yyyy, mm, dd)
                }

                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }
}