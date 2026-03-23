package cat.copernic.odecoches.ui.enregistrament

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.odecoches.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Pantalla principal per al registre de nous usuaris.
 * Gestiona el formulari complet, incloent dades personals, documents d'identitat,
 * llicències de conduir i mètodes de pagament.
 *
 * @param viewModel El ViewModel que gestiona l'estat i la lògica de la pantalla.
 * @param onBack Callback que s'executa en prémer el botó de retrocés.
 * @param onRegistreSuccess Callback que s'executa quan el registre s'ha completat amb èxit.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistreScreen(
    viewModel: RegistreViewModel = viewModel(),
    onBack: () -> Unit,
    onRegistreSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Lògica de validació per habilitar el botó de registre
    val isFormComplete = state.nomComplet.isNotBlank() &&
            state.numIdentificacio.isNotBlank() &&
            state.email.contains("@") && // Validació bàsica de correu electrònic
            state.password.length >= 6 && // Mínim de 6 caràcters per seguretat
            state.adreca.isNotBlank() &&
            state.numTargeta.length == 16 && // La targeta ha de tenir exactament 16 dígits
            state.nacionalitat.isNotBlank() &&
            state.tipusLlicencia.isNotBlank() &&
            state.caducitatLlicencia.isNotBlank() &&
            state.caducitatIdentificacio.isNotBlank()

    // Navega cap a la següent pantalla si el registre és correcte
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegistreSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_register)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_desc)
                        )
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
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.title_register),
                style = MaterialTheme.typography.headlineSmall
            )

            // --- SECCIÓ DE DADES PERSONALS ---
            OutlinedTextField(
                value = state.nomComplet,
                onValueChange = { viewModel.onNomCompletChange(it) },
                label = { Text("${stringResource(R.string.label_full_name)} *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.numIdentificacio,
                    onValueChange = { viewModel.onDniChange(it) },
                    label = { Text("${stringResource(R.string.label_id_number)} *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                DatePickerField(
                    label = "${stringResource(R.string.doc_expiry)} *",
                    value = state.caducitatIdentificacio,
                    onDateSelected = { viewModel.onCaducitatDniChange(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            SearchableNationalityDropdown(
                selected = state.nacionalitat,
                onSelected = { viewModel.onNacionalitatChange(it) }
            )

            // --- SECCIÓ DE DOCUMENT D'IDENTITAT ---
            val idPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { viewModel.onImatgeIdentificacioSelected(it) }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { idPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (state.imatgeIdentificacio != null) "✅ ${stringResource(R.string.btn_id_uploaded)}" else stringResource(R.string.btn_upload_id))
                    }
                    GenericDropdown(
                        label = stringResource(R.string.id_document_title),
                        options = listOf("DNI", "NIE", stringResource(R.string.type_passport)),
                        selected = state.tipusDocument,
                        onSelected = { viewModel.onTipusDocumentChange(it) }
                    )
                }
            }

            // --- SECCIÓ DEL CARNET DE CONDUIR ---
            val licensePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { viewModel.onImatgeLlicenciaSelected(it) }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { licensePicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (state.imatgeLlicencia != null) "✅ ${stringResource(R.string.btn_license_uploaded)}" else stringResource(R.string.btn_upload_license))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            GenericDropdown(
                                label = stringResource(R.string.license_type),
                                options = listOf("AM", "A1", "A2", "A", "B", "B+E", "C"),
                                selected = state.tipusLlicencia,
                                onSelected = { viewModel.onTipusLlicenciaChange(it) }
                            )
                        }
                        DatePickerField(
                            label = stringResource(R.string.license_expiry),
                            value = state.caducitatLlicencia,
                            onDateSelected = { viewModel.onCaducitatLlicenciaChange(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // --- SECCIÓ D'ADREÇA I PAGAMENT ---
            OutlinedTextField(
                value = state.adreca,
                onValueChange = { viewModel.onAdrecaChange(it) },
                label = { Text("${stringResource(R.string.label_address)} *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.numTargeta,
                onValueChange = { viewModel.onNumTargetaChange(it) },
                label = { Text("${stringResource(R.string.label_credit_card)} *") },
                placeholder = { Text("16 dígits") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // --- SECCIÓ DE DADES DEL COMPTE ---
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("${stringResource(R.string.label_email)} *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("${stringResource(R.string.label_password)} *") },
                placeholder = { Text("Mín. 6 caràcters + símbol") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Missatge d'error visual si l'estat en conté un
            if (state.errorMessage != null) {
                Text(
                    text = context.getString(state.errorMessage!!),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // BOTÓ DE REGISTRE: Només s'activa si el formulari és vàlid i no s'està carregant
            Button(
                onClick = { viewModel.registrarUsuari(context) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !state.isLoading && isFormComplete
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text(stringResource(R.string.btn_register_me))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Component desplegable amb cerca per seleccionar la nacionalitat.
 *
 * @param selected La nacionalitat seleccionada actualment.
 * @param onSelected Callback que es dispara en seleccionar una nova nacionalitat.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchableNationalityDropdown(selected: String, onSelected: (String) -> Unit) {
    val allOptions = remember { getNationalitiesList() }
    var searchQuery by remember { mutableStateOf(selected) }
    var expanded by remember { mutableStateOf(false) }
    val filteredOptions = allOptions.filter { it.contains(searchQuery, ignoreCase = true) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                expanded = true
            },
            label = { Text(stringResource(R.string.nationality)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        if (filteredOptions.isNotEmpty()) {
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                filteredOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelected(option)
                            searchQuery = option
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Component desplegable genèric per a seleccions simples.
 *
 * @param label L'etiqueta que descriu el camp.
 * @param options Llista d'opcions disponibles.
 * @param selected L'opció seleccionada actualment.
 * @param onSelected Callback que es dispara en seleccionar una opció.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenericDropdown(label: String, options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}

/**
 * Camp de text que obre un diàleg de selecció de data (DatePicker).
 *
 * @param label L'etiqueta del camp.
 * @param value El valor de la data seleccionada en format text.
 * @param onDateSelected Callback que rep la data seleccionada en format ISO.
 * @param modifier Modificador per personalitzar l'aparença.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(label: String, value: String, onDateSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    var showPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null)
            }
        },
        modifier = modifier
    )

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        onDateSelected(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    }
                    showPicker = false
                }) { Text(stringResource(R.string.generic_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text(stringResource(R.string.generic_cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * Genera una llista de nacionalitats amb les seves banderes corresponents.
 *
 * @return Una llista de cadenes de text amb format "Emoji-Bandera Nom (Nom Nativa)".
 */
private fun getNationalitiesList(): List<String> {
    val paisesPro = listOf(
        "AD", "ES", "FR", "PT", "IT", "DE", "GB", "IE", "US", "CA",
        "MX", "AR", "BR", "CH", "BE", "NL", "AT", "GR", "PL", "RO"
    )

    return paisesPro.map { countryCode ->
        val locale = Locale("", countryCode)
        val flag = countryCode.uppercase().map { char ->
            Character.codePointAt("$char", 0) + 127397
        }.joinToString("") { String(Character.toChars(it)) }

        val systemName = locale.getDisplayCountry(Locale.getDefault())
        val nativeName = locale.getDisplayCountry(locale)

        if (systemName == nativeName) "$flag $systemName"
        else "$flag $systemName ($nativeName)"
    }.sortedBy { it.substring(3) }
}
