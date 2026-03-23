package cat.copernic.odecoches.features.reservations.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cat.copernic.odecoches.features.reservations.data.remote.ReservaResponse
import cat.copernic.odecoches.features.reservations.presentation.viewmodel.MyReservationsViewModel
import androidx.compose.ui.res.stringResource
import cat.copernic.odecoches.R

/**
 * Pantalla que mostra la llista de reserves de l'usuari autenticat.
 *
 * Aquesta pantalla permet:
 * - Visualitzar totes les reserves de l'usuari
 * - Ordenar les reserves per data (ascendent o descendent)
 * - Cercar una reserva pel seu codi identificador
 * - Accedir al detall d'una reserva concreta
 *
 * Les dades es carreguen a través del {@link MyReservationsViewModel}
 * quan la pantalla s'inicialitza.
 *
 * @param userEmail correu electrònic de l'usuari autenticat
 * @param viewModel ViewModel encarregat de gestionar l'estat de la pantalla
 * @param onBack acció per tornar a la pantalla anterior
 * @param onReservationClick acció que s'executa quan l'usuari selecciona una reserva
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    userEmail: String,
    viewModel: MyReservationsViewModel,
    onBack: () -> Unit,
    onReservationClick: (Long) -> Unit

) {
    val state by viewModel.uiState.collectAsState()

    // Carrega al entrar (amb l'ordre actual del state)
    LaunchedEffect(userEmail) {
        viewModel.load(userEmail)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_reservations_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Selector d'ordre (sempre visible)
            var query by rememberSaveable { mutableStateOf("") }
            val filteredItems = remember(state.items, query) {
                if (query.isBlank()) state.items
                else state.items.filter { it.idReserva.toString() == query }
            }
            OutlinedTextField(
                value = query,
                onValueChange = { newValue ->
                    // Solo números (y permitir vacío)
                    query = newValue.filter { it.isDigit() }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.reservation_code)) },
                placeholder = { Text(stringResource(R.string.reservation_code_example)) },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))
            OrderSelector(
                order = state.order,
                onOrderChange = { newOrder ->
                    viewModel.setOrder(userEmail, newOrder)
                }
            )

            Spacer(Modifier.height(12.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Text(text = state.error ?: "Error", color = MaterialTheme.colorScheme.error)
            } else if (state.items.isEmpty()) {
                Text(stringResource(R.string.no_reservations_yet))
            } else {
                if (filteredItems.isEmpty()) {
                    Text(stringResource(R.string.no_reservation_with_code, query))
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(filteredItems) { r ->
                            ReservationCard(
                                r = r,
                                onClick = { onReservationClick(r.idReserva) }
                            )
                        }
                    }
                }
            }
        }
    }
}
/**
 * Component composable que permet seleccionar l'ordre de visualització
 * de les reserves.
 *
 * L'usuari pot escollir entre:
 * - ordre ascendent per data
 * - ordre descendent per data
 *
 * @param order ordre actual de les reserves ("asc" o "desc")
 * @param onOrderChange funció que s'executa quan l'usuari canvia l'ordre
 */
@Composable
private fun OrderSelector(
    order: String,
    onOrderChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterChip(
            selected = order == "asc",
            onClick = { onOrderChange("asc") },
            label = { Text(stringResource(R.string.order_asc)) }
        )

        FilterChip(
            selected = order == "desc",
            onClick = { onOrderChange("desc") },
            label = { Text(stringResource(R.string.order_desc)) }
        )
    }
}
/**
 * Component composable que mostra la informació resumida d'una reserva.
 *
 * La targeta mostra:
 * - matrícula del vehicle
 * - identificador de la reserva
 * - dates de la reserva
 * - import total
 * - fiança associada
 *
 * Quan l'usuari prem la targeta es navega al detall de la reserva.
 *
 * @param r objecte que conté la informació de la reserva
 * @param onClick acció que s'executa quan l'usuari selecciona la targeta
 */
@Composable
private fun ReservationCard(r: ReservaResponse,
                            onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = r.vehicleMatricula ?: "Vehicle",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(stringResource(R.string.reservation_id, r.idReserva.toString()))
            Text(stringResource(R.string.start_date, r.dataInici ?: "-"))
            Text(stringResource(R.string.end_date, r.dataFi ?: "-"))

            Spacer(Modifier.height(8.dp))

            val total = r.importTotal?.let { String.format("%.2f", it) } ?: "-"
            val fianca = r.fiancaPagada?.let { String.format("%.2f", it) } ?: "-"

            Text(stringResource(R.string.total_amount_value, total), style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.deposit_value, fianca), style = MaterialTheme.typography.bodyMedium)
        }
    }
}