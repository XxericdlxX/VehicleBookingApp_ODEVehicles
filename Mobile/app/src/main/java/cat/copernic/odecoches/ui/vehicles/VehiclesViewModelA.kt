package cat.copernic.odecoches.ui.vehicles

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.odecoches.R
import cat.copernic.odecoches.data.repository.VehicleRepository
import cat.copernic.odecoches.domain.model.Vehicle
import cat.copernic.odecoches.features.reservations.data.remote.ReservaCreateRequest
import cat.copernic.odecoches.features.reservations.data.remote.ReservaResponse
import cat.copernic.odecoches.features.reservations.data.repository.ReservaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Defineix les opcions d'ordenació i filtratge disponibles per a la llista de vehicles.
 */
enum class OrdreVehicles { PER_DEFECTE, PREU_ALT, PREU_BAIX, NOMES_DISPONIBLES }

/**
 * Representa l'estat de la interfície d'usuari relatiu a la cerca de disponibilitat per dates.
 *
 * @property dataIniciText Data d'inici en format de text (entrada de l'usuari).
 * @property dataFiText Data de fi en format de text (entrada de l'usuari).
 * @property dataInici Objecte [LocalDate] de la data d'inici.
 * @property dataFi Objecte [LocalDate] de la data de fi.
 * @property isLoading Indica si s'està consultant la disponibilitat al servidor.
 * @property disponiblesMatricules Conjunt de matrícules dels vehicles que estan lliures.
 */
data class AvailabilityUiState(
    val dataIniciText: String = "",
    val dataFiText: String = "",
    val dataInici: LocalDate? = null,
    val dataFi: LocalDate? = null,
    val isLoading: Boolean = false,
    val disponiblesMatricules: Set<String> = emptySet()
)

@RequiresApi(Build.VERSION_CODES.O)
private val ISO = DateTimeFormatter.ISO_LOCAL_DATE

/**
 * ViewModel encarregat de la gestió de la llista de vehicles, filtratge i procés de reserva.
 * Coordina la comunicació entre la interfície d'usuari i els repositoris de dades.
 *
 * @property vehicleRepository Repositori per accedir a la informació dels vehicles.
 * @property reservaRepository Repositori per gestionar les reserves i disponibilitat.
 */
class VehicleViewModel(
    private val vehicleRepository: VehicleRepository,
    private val reservaRepository: ReservaRepository
) : ViewModel() {

    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles.asStateFlow()

    private val _selectedVehicle = MutableStateFlow<Vehicle?>(null)
    val selectedVehicle: StateFlow<Vehicle?> = _selectedVehicle.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _ordreActual = MutableStateFlow(OrdreVehicles.PER_DEFECTE)
    val ordreActual: StateFlow<OrdreVehicles> = _ordreActual.asStateFlow()

    private val _availability = MutableStateFlow(AvailabilityUiState())
    val availability: StateFlow<AvailabilityUiState> = _availability.asStateFlow()

    private val _uiError = MutableStateFlow<Int?>(null)
    val uiError: StateFlow<Int?> = _uiError.asStateFlow()

    private val _previewLoading = MutableStateFlow(false)
    val previewLoading: StateFlow<Boolean> = _previewLoading.asStateFlow()

    private val _preview = MutableStateFlow<ReservaResponse?>(null)
    val preview: StateFlow<ReservaResponse?> = _preview.asStateFlow()

    private val _previewVehicleMatricula = MutableStateFlow<String?>(null)
    val previewVehicleMatricula: StateFlow<String?> = _previewVehicleMatricula.asStateFlow()

    private val _createLoading = MutableStateFlow(false)
    val createLoading: StateFlow<Boolean> = _createLoading.asStateFlow()

    private val _createdReservationId = MutableStateFlow<Long?>(null)
    val createdReservationId: StateFlow<Long?> = _createdReservationId.asStateFlow()

    /**
     * Flux que combina la llista original, la cerca i els filtres per produir la llista final de vehicles.
     */
    val filteredVehicles = combine(_vehicles, _searchQuery, _ordreActual, _availability) { list, query, ordre, avail ->
        val initialFiltered = if (query.isEmpty()) list
        else list.filter { it.matricula.contains(query, ignoreCase = true) }

        val availableSet = avail.disponiblesMatricules

        @RequiresApi(Build.VERSION_CODES.O)
        fun isAvailableForDates(v: Vehicle): Boolean {
            val datesValid = (avail.dataInici != null && avail.dataFi != null && !avail.dataFi.isBefore(avail.dataInici))
            return if (!datesValid) {
                v.estatVehicle == "ALTA"
            } else {
                availableSet.contains(v.matricula)
            }
        }

        when (ordre) {
            OrdreVehicles.PREU_ALT -> initialFiltered.sortedByDescending { it.preuHora }
            OrdreVehicles.PREU_BAIX -> initialFiltered.sortedBy { it.preuHora }
            OrdreVehicles.NOMES_DISPONIBLES -> initialFiltered.filter { isAvailableForDates(it) }
            OrdreVehicles.PER_DEFECTE -> initialFiltered
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        fetchVehicles()
    }

    fun ordenarPerPreuMesAlt() { _ordreActual.value = OrdreVehicles.PREU_ALT }
    fun ordenarPerPreuMesBaix() { _ordreActual.value = OrdreVehicles.PREU_BAIX }
    fun filtrarPerDisponibles() {
        _ordreActual.value = if (_ordreActual.value == OrdreVehicles.NOMES_DISPONIBLES) OrdreVehicles.PER_DEFECTE
        else OrdreVehicles.NOMES_DISPONIBLES
    }
    fun clearAvailability() {
        _availability.value = AvailabilityUiState()
    }

    fun onSearchQueryChange(newQuery: String) { _searchQuery.value = newQuery }
    fun selectVehicle(vehicle: Vehicle) { _selectedVehicle.value = vehicle }

    /**
     * Obté la llista completa de vehicles del servidor.
     */
    fun fetchVehicles() {
        viewModelScope.launch {
            try {
                _vehicles.value = vehicleRepository.getVehicles()
            } catch (e: Exception) {
                _uiError.value = R.string.generic_error
            }
        }
    }

    /**
     * Refresca la informació detallada d'un vehicle concret.
     */
    fun refreshVehicleDetail(matricula: String) {
        viewModelScope.launch {
            try {
                val updatedVehicle = vehicleRepository.getVehicleByMatricula(matricula)
                _selectedVehicle.value = updatedVehicle
            } catch (e: Exception) {
                _uiError.value = R.string.generic_error
            }
        }
    }

    /**
     * Valida i assigna l'interval de dates per a la reserva, activant la cerca de disponibilitat si és correcte.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setDates(iniciText: String, fiText: String) {
        val iniciParsed = parseToLocalDateOrNull(iniciText)
        val fiParsed = parseToLocalDateOrNull(fiText)
        val yearMax = 2060
        val today = LocalDate.now()

        if (iniciParsed != null && fiParsed != null && fiParsed.isBefore(iniciParsed)) {
            _uiError.value = R.string.error_date_order
        }

        if ((iniciParsed != null && iniciParsed.isBefore(today)) ||
            (fiParsed != null && fiParsed.isBefore(today))) {
            _uiError.value = R.string.error_date_past
            return
        }

        if ((iniciParsed?.year ?: 0) > yearMax || (fiParsed?.year ?: 0) > yearMax) {
            _uiError.value = R.string.error_year_limit
        }

        _availability.value = _availability.value.copy(
            dataIniciText = iniciText,
            dataFiText = fiText,
            dataInici = iniciParsed,
            dataFi = fiParsed
        )

        if (iniciParsed != null && fiParsed != null && !fiParsed.isBefore(iniciParsed) &&
            iniciParsed.year <= yearMax && fiParsed.year <= yearMax
        ) {
            fetchDisponibilitat(iniciParsed, fiParsed)
        } else {
            _availability.value = _availability.value.copy(disponiblesMatricules = emptySet(), isLoading = false)
        }
    }

    /**
     * Assigna una data seleccionada des del component visual de calendari.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setDateFromPicker(isStart: Boolean, millis: Long) {
        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        val newText = date.format(ISO)

        if (date.year > 2060) {
            _uiError.value = R.string.error_year_limit
            return
        }

        val current = _availability.value
        if (isStart) setDates(newText, current.dataFiText) else setDates(current.dataIniciText, newText)
    }

    /**
     * Consulta al repositori quins vehicles estan lliures per a un període de temps.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchDisponibilitat(inici: LocalDate, fi: LocalDate) {
        viewModelScope.launch {
            _availability.value = _availability.value.copy(isLoading = true)
            try {
                val list = reservaRepository.vehiclesDisponibles(inici.format(ISO), fi.format(ISO))
                _availability.value = _availability.value.copy(
                    isLoading = false,
                    disponiblesMatricules = list.map { it.matricula }.toSet()
                )
            } catch (e: Exception) {
                _availability.value = _availability.value.copy(isLoading = false, disponiblesMatricules = emptySet())
                _uiError.value = R.string.generic_error
            }
        }
    }

    /**
     * Neteja l'error actual de la interfície.
     */
    fun clearUiError() { _uiError.value = null }

    /**
     * Carrega una previsualització de la reserva abans de la seva confirmació final.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadPreview(vehicleMatricula: String) {
        val inici = _availability.value.dataInici
        val fi = _availability.value.dataFi

        if (inici == null || fi == null) {
            _uiError.value = R.string.select_dates_hint
            return
        }

        viewModelScope.launch {
            _previewVehicleMatricula.value = vehicleMatricula
            _previewLoading.value = true
            _preview.value = null
            try {
                val prev = reservaRepository.previewReserva(
                    ReservaCreateRequest(vehicleMatricula, inici.format(ISO), fi.format(ISO))
                )
                _preview.value = prev
            } catch (e: Exception) {
                _uiError.value = R.string.loading_summary
            } finally {
                _previewLoading.value = false
            }
        }
    }

    /**
     * Sol·licita la creació d'una nova reserva al sistema.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createReservation(vehicleMatricula: String, userEmail: String) {
        val inici = _availability.value.dataInici
        val fi = _availability.value.dataFi

        if (userEmail.isBlank()) {
            _uiError.value = R.string.login_to_confirm
            return
        }

        viewModelScope.launch {
            _createLoading.value = true
            try {
                val res = reservaRepository.crearReserva(
                    userEmail,
                    ReservaCreateRequest(vehicleMatricula, inici!!.format(ISO), fi!!.format(ISO))
                )
                _createdReservationId.value = res.idReserva
                fetchDisponibilitat(inici, fi)
            } catch (e: Exception) {
                _uiError.value = R.string.generic_error
            } finally {
                _createLoading.value = false
            }
        }
    }

    /**
     * Neteja les dades de previsualització i l'estat de creació de reserves.
     */
    fun clearPreview() {
        _previewVehicleMatricula.value = null
        _preview.value = null
        _createdReservationId.value = null
        _previewLoading.value = false
        _createLoading.value = false
    }

    /**
     * Analitza una cadena de text per convertir-la en [LocalDate], suportant diversos formats.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseToLocalDateOrNull(input: String): LocalDate? {
        val t = input.trim().replace('/', '-')
        if (t.isEmpty()) return null
        return try {
            if (Regex("""\d{4}-\d{2}-\d{2}""").matches(t)) LocalDate.parse(t, ISO)
            else if (Regex("""\d{2}-\d{2}-\d{4}""").matches(t)) {
                val parts = t.split("-")
                LocalDate.of(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
            } else null
        } catch (_: Exception) { null }
    }
}
