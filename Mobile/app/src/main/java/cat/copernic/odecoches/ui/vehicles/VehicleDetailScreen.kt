package cat.copernic.odecoches.ui.vehicles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cat.copernic.odecoches.R
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.RoundedCornerShape
import cat.copernic.odecoches.data.remote.RetrofitClient

/**
 * Tradueix el tipus de vehicle provinent de la base de dades a una cadena de text localitzada.
 *
 * @param type El tipus de vehicle en format text (ex: "COTXE").
 * @return La cadena de text traduïda segons els recursos de l'aplicació.
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
 * Tradueix l'estat de disponibilitat del vehicle a una cadena de text localitzada.
 *
 * @param status L'estat del vehicle (ex: "ALTA", "BAIXA").
 * @return El text localitzat que representa l'estat (Disponible/No disponible).
 */
@Composable
private fun getTranslatedStatus(status: String?): String {
    return when (status?.uppercase()) {
        "ALTA" -> stringResource(R.string.status_available)
        "BAIXA" -> stringResource(R.string.status_unavailable)
        else -> stringResource(R.string.status_unknown)
    }
}

/**
 * Pantalla de detall del vehicle.
 * Mostra tota la informació tècnica, d'estat i de lloguer d'un vehicle seleccionat.
 * Inclou la càrrega d'imatges remotes i la visualització de característiques específiques.
 *
 * @param vehicle L'objecte [Vehicle] inicial que es vol mostrar.
 * @param viewModel El ViewModel que gestiona la càrrega i actualització de les dades del vehicle.
 * @param onBack Callback per tornar a la pantalla anterior.
 * @param onNavigateToRegistre Callback per navegar cap a la pantalla de registre d'usuaris.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(
    vehicle: cat.copernic.odecoches.domain.model.Vehicle,
    viewModel: VehicleViewModel,
    onBack: () -> Unit,
    onNavigateToRegistre: () -> Unit
) {
    // Observa l'estat del vehicle seleccionat des del ViewModel
    val liveVehicle by viewModel.selectedVehicle.collectAsState()
    val currentVehicle = liveVehicle ?: vehicle

    // Actualitza les dades del vehicle cada vegada que canvia la matrícula
    LaunchedEffect(currentVehicle.matricula) {
        viewModel.refreshVehicleDetail(currentVehicle.matricula)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Lògica per construir la URL de la imatge del vehicle
            val baseUrl = RetrofitClient.BASE_URL
            val fotoRelativa = currentVehicle.rutaDocumentacioPrivada ?: "/images/vehicles/default.jpg"
            val imageUrl = if (fotoRelativa.startsWith("http")) fotoRelativa else "$baseUrl/${fotoRelativa.trimStart('/')}"

            // Imatge del vehicle amb càrrega asíncrona
            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.vehicle_photo),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Informació principal: Matrícula
            Text(
                text = currentVehicle.matricula,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )

            // Tipus de vehicle i estat actual
            Text(
                text = "${getTranslatedVehicleType(currentVehicle.tipusVehicle)} - ${getTranslatedStatus(currentVehicle.estatVehicle)}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Secció d'especificacions tècniques
            DetailSection(title = stringResource(R.string.section_specs)) {
                DetailItem(stringResource(R.string.label_engine), currentVehicle.motor)
                DetailItem(
                    stringResource(R.string.label_power),
                    stringResource(R.string.power_unit, currentVehicle.potencia ?: "_")
                )
                DetailItem(stringResource(R.string.label_color), currentVehicle.color)
                DetailItem(
                    stringResource(R.string.label_mileage),
                    "${currentVehicle.limitQuilometratge ?: stringResource(R.string.no_limit)} ${stringResource(R.string.unit_km)}"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Secció de condicions de lloguer
            DetailSection(title = stringResource(R.string.section_rental)) {
                DetailItem(stringResource(R.string.label_price_hour), "${currentVehicle.preuHora} ${stringResource(R.string.unit_eur)}")
                DetailItem(stringResource(R.string.label_deposit), "${currentVehicle.fiancaEstandard} ${stringResource(R.string.unit_eur)}")
                DetailItem(stringResource(R.string.label_min_days), "${currentVehicle.minDiesLloguer ?: 1} ${stringResource(R.string.unit_days)}")
                DetailItem(
                    stringResource(R.string.label_max_days),
                    "${currentVehicle.maxDiesLloguer ?: stringResource(R.string.not_defined)} ${stringResource(R.string.unit_days)}"
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * Component visual que agrupa diversos elements de detall sota un títol i dins d'una targeta.
 *
 * @param title El títol de la secció.
 * @param content El contingut composable que es mostrarà dins de la secció.
 */
@Composable
fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}

/**
 * Component que representa una fila amb una etiqueta i el seu valor corresponent.
 *
 * @param label L'etiqueta descriptiva de la dada.
 * @param value El valor de la dada (pot ser nul).
 */
@Composable
fun DetailItem(label: String, value: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value ?: stringResource(R.string.generic_na), style = MaterialTheme.typography.bodyLarge)
    }
}
