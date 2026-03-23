package cat.copernic.odecoches.features.reservations.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cat.copernic.odecoches.domain.model.Vehicle
import cat.copernic.odecoches.features.reservations.presentation.viewmodel.ReservaFlowViewModel
import cat.copernic.odecoches.navigation.Routes
import androidx.compose.ui.res.stringResource
import cat.copernic.odecoches.R
import cat.copernic.odecoches.ui.vehicles.VehicleViewModel
import kotlinx.coroutines.delay
/**
 * Pantalla principal del flux de creació d'una reserva.
 *
 * Aquesta pantalla implementa el procés complet de reserva en tres passos:
 *
 * 1. Introducció de les dates de reserva.
 * 2. Consulta i selecció d'un vehicle disponible.
 * 3. Previsualització del cost i confirmació de la reserva.
 *
 * El flux està gestionat pel {@link ReservaFlowViewModel}, que controla
 * l'estat de la reserva, la càrrega dels vehicles disponibles i la
 * confirmació final de la reserva.
 *
 * Quan la reserva es crea correctament:
 * - es reinicia el flux de reserva
 * - es netegen les dades de disponibilitat
 * - l'usuari és redirigit a la pantalla principal.
 *
 * @param viewModel ViewModel que gestiona la lògica del flux de reserva
 * @param userEmail correu electrònic de l'usuari autenticat (pot ser null si no ha iniciat sessió)
 * @param navController controlador de navegació utilitzat per canviar de pantalla
 * @param vehicleViewModel ViewModel utilitzat per gestionar la disponibilitat dels vehicles
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaFlowRoot(
    viewModel: ReservaFlowViewModel,
    userEmail: String?,
    navController: NavHostController,
    vehicleViewModel: VehicleViewModel
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.reservaCreadaId) {
        if (state.reservaCreadaId != null) {
            delay(1000)

            viewModel.resetFlow()
            vehicleViewModel.clearAvailability()

            navController.navigate(Routes.Home.route) {
                popUpTo(Routes.Home.route)
                launchSingleTop = true
            }
        }
    }

    var inici by remember { mutableStateOf("") }
    var fi by remember { mutableStateOf("") }

    LaunchedEffect(state.dataIniciText, state.dataFiText) {
        inici = state.dataIniciText
        fi = state.dataFiText
    }

    // Snackbar controlado
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_reservation_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // ===== PASO 1: FECHAS =====

            OutlinedTextField(
                value = state.dataIniciText,
                onValueChange = {
                    viewModel.setDates(it, state.dataFiText)
                },
                label = { Text(stringResource(R.string.start_date_format)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.dataFiText,
                onValueChange = {
                    viewModel.setDates(state.dataIniciText, it)
                },
                label = { Text(stringResource(R.string.end_date_format)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { viewModel.buscarDisponibles() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.loading
            ) {
                Text(if (state.loading) stringResource(R.string.searching) else stringResource(R.string.search_available_vehicles))
            }

            Spacer(Modifier.height(16.dp))

            if (state.loading) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
            }

            // ===== PASO 2: LISTA DISPONIBLES =====

            if (state.disponibles.isNotEmpty()) {

                Text(
                    "Selecciona un vehicle:",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.disponibles) { vehicle ->
                        VehicleCardSelectable(
                            vehicle = vehicle,
                            onSelect = {
                                viewModel.seleccionarVehicle(vehicle)
                                viewModel.carregarPreview()
                            }
                        )
                    }
                }
            } else {
                Spacer(Modifier.weight(1f))
            }

            // ===== PASO 3: PREVIEW =====
            state.preview?.let { preview ->
                Spacer(Modifier.height(12.dp))

                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.total_cost_eur, preview.importTotal.toString()))
                        Text(stringResource(R.string.deposit_eur, (preview.fiancaPagada ?: 0).toString()))

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val email = userEmail
                                if (email.isNullOrBlank()) {
                                    navController.navigate(Routes.Login.route)
                                } else {
                                    viewModel.confirmarReserva(email)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.loading && state.reservaCreadaId == null
                        ) {
                            Text(if (state.loading) stringResource(R.string.creating) else stringResource(R.string.confirm_reservation))
                        }
                    }
                }
            }

            // ===== RESERVA CREADA =====
            state.reservaCreadaId?.let { id ->
                Spacer(Modifier.height(12.dp))
                Text(
                    "Reserva creada! Codi: $id",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )

            }
        }
    }
}
/**
 * Component composable que mostra una targeta amb la informació
 * bàsica d'un vehicle disponible per reservar.
 *
 * La targeta permet seleccionar el vehicle per continuar amb el
 * procés de reserva.
 *
 * Mostra:
 * - matrícula del vehicle
 * - tipus de vehicle
 * - preu per hora
 *
 * @param vehicle vehicle que es mostra a la targeta
 * @param onSelect acció que s'executa quan l'usuari selecciona el vehicle
 */
@Composable
private fun VehicleCardSelectable(
    vehicle: Vehicle,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(vehicle.matricula, style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.type, vehicle.tipusVehicle))
            Text(stringResource(R.string.eur_per_hour, vehicle.preuHora.toString()))
        }
    }
}
