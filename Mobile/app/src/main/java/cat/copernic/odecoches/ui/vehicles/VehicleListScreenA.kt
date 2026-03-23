package cat.copernic.odecoches.ui.vehicles

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cat.copernic.odecoches.R
import cat.copernic.odecoches.core.session.SessionManager
import cat.copernic.odecoches.data.remote.RetrofitClient
import cat.copernic.odecoches.domain.model.Vehicle
import cat.copernic.odecoches.ui.theme.BluePrimary
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Pantalla principal que mostra el llistat de vehicles disponibles.
 * Inclou funcionalitats de cerca en temps real, filtratge per preu i disponibilitat,
 * així com la selecció de dates per a la reserva.
 *
 * @param viewModel El ViewModel que gestiona l'estat de la llista i els filtres.
 * @param onNavigateToDetail Callback per navegar als detalls d'un vehicle.
 * @param onNavigateToReservaConfirm Callback per anar a la confirmació de la reserva.
 * @param onRequireLogin Callback que s'executa si l'usuari intenta reservar sense estar autenticat.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VehicleListScreen(
    viewModel: VehicleViewModel,
    onNavigateToDetail: (Vehicle) -> Unit,
    onNavigateToReservaConfirm: (Vehicle) -> Unit,
    onRequireLogin: () -> Unit
) {
    // Bucle de refresc automàtic per mantenir la llista actualitzada cada 1.5 segons
    LaunchedEffect(Unit) {
        while(true) {
            viewModel.fetchVehicles()
            delay(1500)
        }
    }

    val vehicles by viewModel.filteredVehicles.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val ordreActual by viewModel.ordreActual.collectAsState()
    val availability by viewModel.availability.collectAsState()
    val uiError by viewModel.uiError.collectAsState()
    val context = LocalContext.current

    val snack = remember { SnackbarHostState() }
    val isLoggedIn = !SessionManager.userEmail.isNullOrBlank()

    // Gestió visual dels errors mitjançant Snackbar
    LaunchedEffect(uiError) {
        uiError?.let { errorRes ->
            snack.showSnackbar(context.getString(errorRes))
            viewModel.clearUiError()
        }
    }

    VehicleListContent(
        vehicles = vehicles,
        searchQuery = searchQuery,
        ordreActual = ordreActual,
        availability = availability,
        isLoggedIn = isLoggedIn,
        onSearchChange = viewModel::onSearchQueryChange,
        onSortCheapest = viewModel::ordenarPerPreuMesBaix,
        onSortExpensive = viewModel::ordenarPerPreuMesAlt,
        onFilterAvailable = viewModel::filtrarPerDisponibles,
        onDatesChange = viewModel::setDates,
        onPickDate = viewModel::setDateFromPicker,
        onNavigateToDetail = onNavigateToDetail,
        onReserveClick = { v -> onNavigateToReservaConfirm(v) },
        onRequireLogin = onRequireLogin,
        snackbarHostState = snack
    )
}

/**
 * Tradueix el tipus de vehicle a una cadena de text localitzada.
 */
@Composable
private fun getTranslatedVehicleType(type: String): String {
    return when (type.uppercase()) {
        "COTXE" -> stringResource(R.string.type_car)
        "MOTO" -> stringResource(R.string.type_motorcycle)
        "FURGONETA" -> stringResource(R.string.type_van)
        "TANC" -> stringResource(R.string.type_tank)
        else -> type
    }
}

/**
 * Estructura visual del contingut de la llista de vehicles.
 * Conté la barra superior, el cercador, els selectors de dates i els xips de filtratge.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListContent(
    vehicles: List<Vehicle>,
    searchQuery: String,
    ordreActual: OrdreVehicles,
    availability: AvailabilityUiState,
    isLoggedIn: Boolean,
    onSearchChange: (String) -> Unit,
    onSortCheapest: () -> Unit,
    onSortExpensive: () -> Unit,
    onFilterAvailable: () -> Unit,
    onDatesChange: (String, String) -> Unit,
    onPickDate: (Boolean, Long) -> Unit,
    onNavigateToDetail: (Vehicle) -> Unit,
    onReserveClick: (Vehicle) -> Unit,
    onRequireLogin: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BluePrimary
                    )
                )

                // Camp de cerca per filtrar vehicles per matrícula o característiques
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                // Secció de selecció de dates per comprovar disponibilitat real
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DateField(
                        label = stringResource(R.string.start_date_short),
                        value = availability.dataIniciText,
                        onValueChange = { onDatesChange(it, availability.dataFiText) },
                        isStart = true,
                        onPickDate = onPickDate
                    )

                    DateField(
                        label = stringResource(R.string.end_date_short),
                        value = availability.dataFiText,
                        onValueChange = { onDatesChange(availability.dataIniciText, it) },
                        isStart = false,
                        onPickDate = onPickDate
                    )

                    if (availability.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }

                // Xips de filtratge i ordenació
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = ordreActual == OrdreVehicles.PREU_BAIX,
                        onClick = onSortCheapest,
                        label = { Text(stringResource(R.string.filter_cheapest)) }
                    )
                    FilterChip(
                        selected = ordreActual == OrdreVehicles.PREU_ALT,
                        onClick = onSortExpensive,
                        label = { Text(stringResource(R.string.filter_expensive)) }
                    )
                    FilterChip(
                        selected = ordreActual == OrdreVehicles.NOMES_DISPONIBLES,
                        onClick = onFilterAvailable,
                        label = { Text(stringResource(R.string.filter_available)) }
                    )
                }

                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    ) { paddingValues ->
        if (vehicles.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_vehicles), style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            // Validació de l'interval de dates seleccionat
            val datesValid = availability.dataInici != null && availability.dataFi != null &&
                    !availability.dataFi!!.isBefore(availability.dataInici)

            LazyColumn(
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(vehicles, key = { it.matricula }) { vehicle ->
                    // Determina si el vehicle està disponible segons les dates o l'estat general
                    val availableForDates = if (!datesValid) (vehicle.estatVehicle == "ALTA")
                    else availability.disponiblesMatricules.contains(vehicle.matricula)

                    VehicleItem(
                        vehicle = vehicle,
                        isAvailable = availableForDates,
                        datesValid = datesValid,
                        isLoggedIn = isLoggedIn,
                        onDetailClick = { onNavigateToDetail(vehicle) },
                        onReserveClick = { onReserveClick(vehicle) },
                        onRequireLogin = onRequireLogin
                    )
                }
            }
        }
    }
}

/**
 * Component per a l'entrada de dates amb un selector de calendari integrat.
 *
 * @param label Etiqueta del camp.
 * @param value Valor actual de la data en format text.
 * @param onValueChange Callback per quan el text canvia manualment.
 * @param isStart Indica si és la data d'inici o de fi.
 * @param onPickDate Callback que s'executa en seleccionar una data del calendari.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isStart: Boolean,
    onPickDate: (Boolean, Long) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.date_label_format, label)) },
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.calendar))
            }
        },
        singleLine = true
    )

    if (showPicker) {
        val today = LocalDate.now()

        val state = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    // Limitem la selecció entre avui i l'any 2060
                    return date.year <= 2060 && !date.isBefore(today)
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year in today.year..2060
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { onPickDate(isStart, it) }
                    showPicker = false
                }) { Text(stringResource(R.string.generic_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text(stringResource(R.string.generic_cancel)) }
            }
        ) {
            DatePicker(state = state, showModeToggle = false)
        }
    }
}

/**
 * Representació visual d'un vehicle individual dins de la llista.
 * Mostra la imatge, matrícula, preu i estat de disponibilitat.
 *
 * @param vehicle El vehicle a mostrar.
 * @param isAvailable Indica si el vehicle està disponible per a les dates seleccionades.
 * @param datesValid Indica si l'interval de dates actual és correcte.
 * @param isLoggedIn Indica si l'usuari ha iniciat sessió.
 */
@Composable
fun VehicleItem(
    vehicle: Vehicle,
    isAvailable: Boolean,
    datesValid: Boolean,
    isLoggedIn: Boolean,
    onDetailClick: () -> Unit,
    onReserveClick: () -> Unit,
    onRequireLogin: () -> Unit
) {
    val imageUrl = RetrofitClient.BASE_URL + (vehicle.rutaDocumentacioPrivada ?: "/images/vehicles/default.jpg")

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(MaterialTheme.shapes.large)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "${vehicle.matricula} - ${getTranslatedVehicleType(vehicle.tipusVehicle)}", style = MaterialTheme.typography.titleLarge)

                        val statusColor = if (isAvailable) Color(0xFF4CAF50) else Color(0xFFF44336)
                        val statusText = if (!datesValid) {
                            if (vehicle.estatVehicle == "ALTA") stringResource(R.string.status_available) else stringResource(R.string.status_unavailable)
                        } else {
                            if (isAvailable) stringResource(R.string.status_available) else stringResource(R.string.not_available)
                        }

                        Surface(
                            color = statusColor.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = statusText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = statusColor
                            )
                        }

                        Text(
                            text = stringResource(R.string.eur_per_hour, vehicle.preuHora.toString()),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Button(onClick = onDetailClick) { Text(stringResource(R.string.details_button)) }
                        Spacer(Modifier.height(8.dp))

                        if (datesValid && isAvailable) {
                            Button(onClick = onReserveClick) {
                                Text(stringResource(R.string.btn_register_book))
                            }
                        } else {
                            AssistChip(
                                onClick = {},
                                label = { Text(if (!datesValid) stringResource(R.string.select_dates_hint) else stringResource(R.string.not_available)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
