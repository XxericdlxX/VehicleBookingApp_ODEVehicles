package cat.copernic.odecoches.features.reservations.presentation.screen

import android.os.Build

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import cat.copernic.odecoches.features.reservations.presentation.viewmodel.ReservationDetailViewModel
import coil.request.ImageRequest
import java.time.LocalDate
import androidx.compose.ui.res.stringResource
import cat.copernic.odecoches.R
import cat.copernic.odecoches.data.remote.RetrofitClient
import cat.copernic.odecoches.features.reservations.presentation.viewmodel.ReservationDetailUiState

/**
 * Construeix la URL completa de la imatge del vehicle.
 *
 * Si la ruta ja és una URL completa (http o https) es retorna directament.
 * En cas contrari, es concatena amb la URL base del backend.
 *
 * @param baseUrl URL base del servidor
 * @param path ruta relativa de la imatge
 * @return URL completa de la imatge o null si no hi ha ruta
 */
private fun buildVehicleImageUrl(baseUrl: String, path: String?): String? {
    if (path.isNullOrBlank()) return null
    if (path.startsWith("http://") || path.startsWith("https://")) return path

    val cleanBase = baseUrl.trimEnd('/')
    val cleanPath = if (path.startsWith("/")) path else "/$path"
    return cleanBase + cleanPath
}
/**
 * Pantalla que mostra el detall complet d'una reserva.
 *
 * Aquesta pantalla permet visualitzar tota la informació d'una reserva
 * concreta, incloent:
 * - dades de la reserva (codi, client, dates, import total i fiança)
 * - informació del vehicle associat
 * - fotografia del vehicle si està disponible
 *
 * També permet anul·lar la reserva si aquesta encara no ha començat.
 * Quan la cancel·lació es realitza correctament, es mostra un missatge
 * informatiu i es retorna a la pantalla anterior.
 *
 * @param reservationId identificador de la reserva que es vol consultar
 * @param userEmail correu electrònic de l'usuari autenticat
 * @param viewModel ViewModel encarregat de gestionar l'estat del detall de la reserva
 * @param onBack acció per tornar a la pantalla anterior
 * @param onCancelled acció que s'executa quan la reserva ha estat anul·lada
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(
    reservationId: Long,
    userEmail: String,
    viewModel: ReservationDetailViewModel,
    onBack: () -> Unit,
    onCancelled: () -> Unit = onBack // opcional: para refrescar lista desde NavGraph
) {
    val state by viewModel.uiState.collectAsState()
    val snack = remember { SnackbarHostState() }
    var showConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(reservationId, userEmail) {
        viewModel.load(reservationId, userEmail)
    }

    val reservationCancelledText = stringResource(R.string.reservation_cancelled)
    val reservationCancelFailedText = stringResource(R.string.reservation_cancel_failed)

    LaunchedEffect(state.cancelOk) {
        if (state.cancelOk?.anulada == true) {
            val msg = state.cancelOk?.missatge ?: reservationCancelledText
            val refund = state.cancelOk?.importRetornat ?: 0.0
            snack.showSnackbar("$msg · Return: ${"%.2f".format(refund)} €")
            onCancelled()
        }
    }

    LaunchedEffect(state.cancelError) {
        state.cancelError?.let { err ->
            snack.showSnackbar(err.ifBlank { reservationCancelFailedText })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reservation_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snack) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.error != null -> Text(
                    text = state.error ?: "Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )

                state.data != null -> {
                    val allowed = canCancel(state.data?.dataInici)

                    DetailContent(state)

                    // Botón abajo (mejor UX que meterlo dentro de la card)
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        if (allowed) {
                            Button(
                                onClick = { showConfirm = true },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isCancelling,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text(if (state.isCancelling) stringResource(R.string.cancelling) else stringResource(R.string.cancel_reservation))
                            }
                        } else {
                            AssistChip(
                                onClick = {},
                                label = { Text(stringResource(R.string.cannot_cancel_reason)) }
                            )
                        }
                    }

                    if (showConfirm) {
                        AlertDialog(
                            onDismissRequest = { showConfirm = false },
                            title = { Text(stringResource(R.string.confirm_cancellation)) },
                            text = { Text(stringResource(R.string.confirm_cancellation_body)) },
                            confirmButton = {
                                TextButton(
                                    enabled = !state.isCancelling,
                                    onClick = {
                                        showConfirm = false
                                        viewModel.cancel(reservationId, userEmail)
                                    }
                                ) { Text(stringResource(R.string.yes_cancel)) }
                            },
                            dismissButton = {
                                TextButton(onClick = { showConfirm = false }) { Text(stringResource(R.string.generic_cancel)) }
                            }
                        )
                    }
                }
            }
        }
    }
}
/**
 * Component composable que mostra el contingut detallat d'una reserva.
 *
 * Inclou:
 * - imatge del vehicle
 * - informació de la reserva
 * - informació del vehicle associat
 *
 * @param state estat actual del detall de la reserva
 */
@Composable
private fun DetailContent(state: ReservationDetailUiState) {
    val d = state.data ?: return
    val ctx = LocalContext.current

    val imageUrl = buildVehicleImageUrl(
        RetrofitClient.BASE_URL,
        d.vehicleFoto
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (imageUrl != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(ctx)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.vehicle_photo),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val id = d.idReserva ?: "-"
                val client = d.clientEmail ?: "-"
                val start = d.dataInici ?: "-"
                val end = d.dataFi ?: "-"
                val total = d.importTotal ?: "-"
                val dep = d.fianca ?: "-"

                Text(stringResource(R.string.reservation_number, id), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.client, client))
                Text(stringResource(R.string.start_date, start))
                Text(stringResource(R.string.end_date, end))
                Text(stringResource(R.string.total_amount, total))
                Text(stringResource(R.string.deposit, dep))
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stringResource(R.string.vehicle_section), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.plate, d.vehicleMatricula ?: "-"))
                Text(stringResource(R.string.type, d.vehicleTipus ?: "-"))
                Text(stringResource(R.string.engine, d.vehicleMotor ?: "-"))
                Text(stringResource(R.string.power, d.vehiclePotencia ?: "-"))
                Text(stringResource(R.string.color, d.vehicleColor ?: "-"))
                Text(stringResource(R.string.price_per_hour, d.vehiclePreuHora ?: "-"))
            }
        }

        Spacer(Modifier.height(70.dp)) // deja espacio para el botón abajo
    }
}
/**
 * Comprova si una reserva es pot anul·lar.
 *
 * Una reserva només es pot anul·lar si la seva data d'inici
 * és posterior a la data actual.
 *
 * @param dataInici data d'inici de la reserva en format ISO (YYYY-MM-DD)
 * @return true si la reserva es pot anul·lar, false en cas contrari
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun canCancel(dataInici: String?): Boolean {
    return try {
        val start = LocalDate.parse(dataInici) // "2027-08-25"
        start.isAfter(LocalDate.now())
    } catch (e: Exception) {
        false
    }
}