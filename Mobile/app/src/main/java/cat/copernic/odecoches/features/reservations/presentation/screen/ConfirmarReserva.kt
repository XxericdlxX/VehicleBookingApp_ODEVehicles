package cat.copernic.odecoches.features.reservations.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cat.copernic.odecoches.domain.model.Vehicle
import cat.copernic.odecoches.features.reservations.presentation.viewmodel.ReservaFlowUiState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import cat.copernic.odecoches.R
import cat.copernic.odecoches.data.remote.RetrofitClient
import kotlinx.coroutines.delay
/**
 * Pantalla de confirmació de reserva (pas 3 del flux de reserva).
 *
 * Aquesta pantalla permet a l'usuari revisar la informació de la reserva
 * abans de confirmar-la. Mostra:
 * - les dates seleccionades
 * - el vehicle escollit
 * - el cost total calculat
 * - la fiança de la reserva
 *
 * També permet:
 * - calcular el cost de la reserva
 * - confirmar la reserva
 * - redirigir a login si l'usuari no està autenticat
 *
 * Quan la reserva es crea correctament, es mostra un missatge informatiu
 * i l'aplicació redirigeix l'usuari a la pantalla principal.
 *
 * @param state estat actual del flux de reserva
 * @param userEmail correu electrònic de l'usuari autenticat
 * @param onLoadPreview acció per calcular el cost de la reserva
 * @param onConfirm acció per confirmar la reserva
 * @param onGoToLogin acció per redirigir a la pantalla de login
 * @param onGoHome acció per tornar a la pantalla principal
 * @param onResetFlow acció per reiniciar el flux de reserva
 */
@Composable
fun ReservaStep3Screen(
    state: ReservaFlowUiState,
    userEmail: String?,
    onLoadPreview: () -> Unit,
    onConfirm: () -> Unit,
    onGoToLogin: () -> Unit,
    onGoHome: () -> Unit,
    onResetFlow: () -> Unit
){
    val v = state.vehicleSeleccionat
    val isLogged = !userEmail.isNullOrBlank()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val importTotal = state.preview?.importTotal ?: 0.0
    val fianca = state.preview?.fiancaPagada ?: 0.0

    LaunchedEffect(state.reservaCreadaId) {
        if (state.reservaCreadaId != null) {

            snackbarHostState.showSnackbar(
                message = context.getString(R.string.reservation_created_email_sent)
            )
            delay(1000)

            onResetFlow()
            onGoHome()
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.step3_confirmation), style = MaterialTheme.typography.titleLarge)

                Text(stringResource(R.string.dates_range, state.dataIniciText, state.dataFiText))

                v?.let {
                    VehicleSummaryCard(vehicle = it)
                } ?: run {
                    Text(
                        "No hi ha cap vehicle seleccionat",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(onClick = onLoadPreview, enabled = !state.loading && state.preview == null) {
                    Text(stringResource(R.string.calculate_cost_deposit))
                }

                state.preview?.let { p ->
                    Text(
                        stringResource(R.string.amount_calculated,importTotal),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        stringResource(R.string.deposit_reservation,fianca),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Confirmar: si NO está logueado -> ir a login
                Button(
                    onClick = { if (isLogged) onConfirm() else onGoToLogin() },
                    enabled = !state.loading && state.preview != null && state.reservaCreadaId == null
                ) {
                    Text(if (state.loading) stringResource(R.string.creating) else if (isLogged) stringResource(R.string.confirm_reservation) else stringResource(R.string.login_to_confirm))
                }
                state.reservaCreadaId?.let { id ->
                    Text(
                        (stringResource(R.string.reservation_check,id)),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        (stringResource(R.string.email_sent_check)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
/**
 * Component composable que mostra un resum del vehicle seleccionat.
 *
 * Inclou la imatge del vehicle i la informació principal com:
 * - matrícula
 * - tipus de vehicle
 * - estat
 * - motor
 * - preu per hora
 * - fiança
 *
 * @param vehicle vehicle que es vol mostrar a la targeta
 */
@Composable
fun VehicleSummaryCard(vehicle: Vehicle) {
    val imageUrl = RetrofitClient.BASE_URL + vehicle.rutaDocumentacioPrivada

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(6.dp))

            Text(
                text = vehicle.matricula,
                style = MaterialTheme.typography.titleLarge
            )

            VehicleInfoRow(stringResource(R.string.type).substringBefore(":"), vehicle.tipusVehicle)
            VehicleInfoRow(stringResource(R.string.status, "").substringBefore(":"), vehicle.estatVehicle ?: "")
            VehicleInfoRow(stringResource(R.string.engine).substringBefore(":"), vehicle.motor ?: "")

            Divider()

            VehicleInfoRow(stringResource(R.string.price_per_hour_label), "${vehicle.preuHora} €")
            VehicleInfoRow(stringResource(R.string.deposit_label), "${vehicle.fiancaEstandard} €")

            Spacer(Modifier.height(2.dp))
        }
    }
}
/**
 * Component composable que mostra una fila d'informació d'un vehicle.
 *
 * S'utilitza per mostrar una etiqueta i el seu valor corresponent
 * dins de la targeta de resum del vehicle.
 *
 * @param label etiqueta descriptiva del camp
 * @param value valor associat a l'etiqueta
 */

@Composable
fun VehicleInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}