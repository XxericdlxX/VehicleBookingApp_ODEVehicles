package cat.copernic.odecoches.ui.profile

import android.app.DatePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.odecoches.R
import cat.copernic.odecoches.core.ui.UiText
import cat.copernic.odecoches.core.utils.ImageBase64
import java.util.Calendar
import java.util.Locale

/**
 * Mostra la pantalla d'edició del perfil i connecta la interfície amb el ViewModel.
 *
 * @param onBack acció per tornar enrere
 * @param onProfileUpdated acció a executar quan el perfil s'actualitza
 * @param onPasswordRecovery acció per navegar a la recuperació de contrasenya
 * @param viewModel ViewModel associat a la pantalla
 */
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onProfileUpdated: () -> Unit,
    onPasswordRecovery: () -> Unit = {},
    viewModel: EditProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val ctx = LocalContext.current

    val pickProfilePhoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val b64 = ImageBase64.uriToBase64Jpeg(ctx, uri)
            val name = ImageBase64.getFileName(ctx, uri) ?: "perfil.jpg"
            if (b64 == null) {
                viewModel.update { s ->
                    s.copy(
                        error = UiText.StringResource(R.string.error_reading_image),
                        successMessage = null
                    )
                }
            } else {
                viewModel.update { s ->
                    s.copy(
                        profilePhotoName = name,
                        profilePhotoBase64 = b64,
                        error = null,
                        successMessage = null
                    )
                }
            }
        }
    }

    val pickDocumentImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val b64 = ImageBase64.uriToBase64Jpeg(ctx, uri)
            val name = ImageBase64.getFileName(ctx, uri) ?: "document.jpg"
            if (b64 == null) {
                viewModel.update { s ->
                    s.copy(
                        error = UiText.StringResource(R.string.error_reading_document_image),
                        successMessage = null
                    )
                }
            } else {
                viewModel.update { s ->
                    s.copy(
                        documentImageName = name,
                        documentImageBase64 = b64,
                        error = null,
                        successMessage = null
                    )
                }
            }
        }
    }

    val pickDriverLicenseImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val b64 = ImageBase64.uriToBase64Jpeg(ctx, uri)
            val name = ImageBase64.getFileName(ctx, uri) ?: "carnet.jpg"
            if (b64 == null) {
                viewModel.update { s ->
                    s.copy(
                        error = UiText.StringResource(R.string.error_reading_license_image),
                        successMessage = null
                    )
                }
            } else {
                viewModel.update { s ->
                    s.copy(
                        driverLicenseImageName = name,
                        driverLicenseImageBase64 = b64,
                        error = null,
                        successMessage = null
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadFromServer()
    }

    EditProfileScreenContent(
        state = state,
        onBack = onBack,
        onPasswordRecovery = onPasswordRecovery,
        onPickProfilePhoto = { pickProfilePhoto.launch("image/*") },
        onPickDocumentImage = { pickDocumentImage.launch("image/*") },
        onPickDriverLicenseImage = { pickDriverLicenseImage.launch("image/*") },
        onFullNameChange = { v ->
            viewModel.update { s ->
                s.copy(fullName = v, error = null, successMessage = null)
            }
        },
        onAddressChange = { v ->
            viewModel.update { s ->
                s.copy(address = v, error = null, successMessage = null)
            }
        },
        onNationalityChange = { v ->
            viewModel.update { s ->
                s.copy(nationality = v, error = null, successMessage = null)
            }
        },
        onDocumentIdChange = { v ->
            viewModel.update { s ->
                s.copy(documentId = v.uppercase(), error = null, successMessage = null)
            }
        },
        onDocumentExpiryChange = { v ->
            viewModel.update { s ->
                s.copy(documentExpiry = v, error = null, successMessage = null)
            }
        },
        onDriverLicenseTypeChange = { v ->
            viewModel.update { s ->
                s.copy(driverLicenseType = v, error = null, successMessage = null)
            }
        },
        onDriverLicenseExpiryChange = { v ->
            viewModel.update { s ->
                s.copy(driverLicenseExpiry = v, error = null, successMessage = null)
            }
        },
        onCreditCardNumberChange = { v ->
            viewModel.update { s ->
                s.copy(creditCardNumber = v.filter { it.isDigit() }.take(16), error = null, successMessage = null)
            }
        },
        onSave = {
            viewModel.saveToServer(onSuccess = onProfileUpdated)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Renderitza el contingut principal del formulari d'edició del perfil.
 */
@Composable
private fun EditProfileScreenContent(
    state: EditProfileUiState,
    onBack: () -> Unit,
    onPasswordRecovery: () -> Unit,
    onPickProfilePhoto: () -> Unit,
    onPickDocumentImage: () -> Unit,
    onPickDriverLicenseImage: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onNationalityChange: (String) -> Unit,
    onDocumentIdChange: (String) -> Unit,
    onDocumentExpiryChange: (String) -> Unit,
    onDriverLicenseTypeChange: (String) -> Unit,
    onDriverLicenseExpiryChange: (String) -> Unit,
    onCreditCardNumberChange: (String) -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    var documentNationality by remember(state.nationality) {
        mutableStateOf(mapNationalityToDropdownValue(state.nationality))
    }

    fun showDatePicker(currentValue: String, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val parts = currentValue.split("-")
        if (parts.size == 3) {
            parts[0].toIntOrNull()?.let { calendar.set(Calendar.YEAR, it) }
            parts[1].toIntOrNull()?.let { calendar.set(Calendar.MONTH, it - 1) }
            parts[2].toIntOrNull()?.let { calendar.set(Calendar.DAY_OF_MONTH, it) }
        }

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected(
                    String.format(
                        Locale.ROOT,
                        "%04d-%02d-%02d",
                        year,
                        month + 1,
                        dayOfMonth
                    )
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ProfilePhotoCard(
                    fileName = state.profilePhotoName,
                    enabled = !state.isLoading,
                    onPick = onPickProfilePhoto
                )
            }

            item { SectionTitle(stringResource(R.string.profile_section_personal_data)) }

            item {
                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = onFullNameChange,
                    label = { Text(stringResource(R.string.full_name_label)) },
                    leadingIcon = { Icon(Icons.Default.AccountCircle, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !state.isLoading,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            item {
                OutlinedTextField(
                    value = state.email,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.email_not_editable_label)) },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = false
                )
            }

            item {
                OutlinedTextField(
                    value = state.address,
                    onValueChange = onAddressChange,
                    label = { Text(stringResource(R.string.address_label)) },
                    leadingIcon = { Icon(Icons.Default.Home, null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )
            }

            item {
                SearchableNationalityDropdown(
                    selected = documentNationality,
                    enabled = !state.isLoading,
                    onSelected = { selectedNationality ->
                        documentNationality = selectedNationality
                        onNationalityChange(selectedNationality)
                    }
                )
            }

            item { SectionTitle(stringResource(R.string.profile_section_documents)) }

            item {
                OutlinedTextField(
                    value = state.documentId,
                    onValueChange = onDocumentIdChange,
                    label = { Text(stringResource(R.string.document_id_label)) },
                    leadingIcon = { Icon(Icons.Default.Fingerprint, null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Next
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = state.documentExpiry,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.document_expiry_label)) },
                    leadingIcon = { Icon(Icons.Default.Event, null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    trailingIcon = {
                        IconButton(
                            enabled = !state.isLoading,
                            onClick = {
                                showDatePicker(state.documentExpiry, onDocumentExpiryChange)
                            }
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null)
                        }
                    }
                )
            }

            item {
                Button(
                    onClick = onPickDocumentImage,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (state.documentImageName.isNullOrBlank()) {
                            stringResource(R.string.upload_document)
                        } else {
                            stringResource(R.string.change_document)
                        }
                    )
                }
            }

            item { SectionTitle(stringResource(R.string.profile_section_license)) }

            item {
                GenericDropdown(
                    label = stringResource(R.string.license_type_label),
                    options = EditProfileViewModel.LICENSE_TYPES,
                    selected = state.driverLicenseType,
                    enabled = !state.isLoading,
                    onSelected = onDriverLicenseTypeChange
                )
            }

            item {
                OutlinedTextField(
                    value = state.driverLicenseExpiry,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.license_expiry_label)) },
                    leadingIcon = { Icon(Icons.Default.Event, null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    trailingIcon = {
                        IconButton(
                            enabled = !state.isLoading,
                            onClick = {
                                showDatePicker(
                                    state.driverLicenseExpiry,
                                    onDriverLicenseExpiryChange
                                )
                            }
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null)
                        }
                    }
                )
            }

            item {
                Button(
                    onClick = onPickDriverLicenseImage,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (state.driverLicenseImageName.isNullOrBlank()) {
                            stringResource(R.string.upload_license)
                        } else {
                            stringResource(R.string.change_license)
                        }
                    )
                }
            }

            item { SectionTitle(stringResource(R.string.profile_section_payment)) }

            item {
                OutlinedTextField(
                    value = state.creditCardNumber,
                    onValueChange = onCreditCardNumberChange,
                    label = { Text(stringResource(R.string.credit_card_label)) },
                    leadingIcon = { Icon(Icons.Default.CreditCard, null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }

            item { SectionTitle(stringResource(R.string.profile_section_security)) }

            item {
                Text(
                    text = stringResource(R.string.password_recovery_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.padding(4.dp))
                Button(
                    onClick = onPasswordRecovery,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.recover_password_button))
                }
            }

            item {
                state.error?.let {
                    Text(
                        text = it.asString(context),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.padding(4.dp))
                }

                state.successMessage?.let {
                    Text(
                        text = it.asString(context),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.padding(4.dp))
                }

                Button(
                    onClick = onSave,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator()
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.saving))
                    } else {
                        Text(stringResource(R.string.save_changes))
                    }
                }
            }
        }
    }
}

/**
 * Mostra el títol d'una secció del formulari.
 *
 * @param text text a visualitzar
 */
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium
    )
}

/**
 * Mostra la targeta de la fotografia de perfil i l'acció per carregar-ne una de nova.
 */
@Composable
private fun ProfilePhotoCard(
    fileName: String?,
    enabled: Boolean,
    onPick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(R.string.profile_photo_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = fileName ?: stringResource(R.string.no_file_selected),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onPick,
                enabled = enabled,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Upload, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (fileName.isNullOrBlank()) {
                        stringResource(R.string.select_file)
                    } else {
                        stringResource(R.string.change_file)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Mostra un desplegable cercable per seleccionar la nacionalitat.
 */
@Composable
private fun SearchableNationalityDropdown(
    selected: String,
    enabled: Boolean,
    onSelected: (String) -> Unit
) {
    val allOptions = remember { getNationalitiesList() }

    var textValue by remember(selected) { mutableStateOf(selected) }
    var expanded by remember { mutableStateOf(false) }
    var isUserTyping by remember { mutableStateOf(false) }

    val filteredOptions = remember(textValue, isUserTyping, allOptions) {
        if (!isUserTyping || textValue.isBlank()) {
            allOptions
        } else {
            allOptions.filter { it.contains(textValue, ignoreCase = true) }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) {
                expanded = !expanded
                if (expanded) {
                    isUserTyping = false
                }
            }
        }
    ) {
        OutlinedTextField(
            value = textValue,
            onValueChange = {
                if (enabled) {
                    textValue = it
                    isUserTyping = true
                    expanded = true
                }
            },
            label = { Text(stringResource(R.string.nationality_label)) },
            leadingIcon = { Icon(Icons.Default.Badge, null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryEditable,
                    enabled = enabled
                )
                .fillMaxWidth(),
            enabled = enabled,
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filteredOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        textValue = option
                        isUserTyping = false
                        expanded = false
                        onSelected(option)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Mostra un desplegable genèric reutilitzable per a l'edició del perfil.
 */
@Composable
private fun GenericDropdown(
    label: String,
    options: List<String>,
    selected: String,
    enabled: Boolean,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(Icons.Default.DirectionsCar, null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = enabled
                )
                .fillMaxWidth(),
            enabled = enabled,
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Normalitza la nacionalitat per adaptar-la al valor mostrat al desplegable.
 *
 * @param rawValue valor rebut o introduït
 * @return valor normalitzat per a la interfície
 */
private fun mapNationalityToDropdownValue(rawValue: String): String {
    if (rawValue.isBlank()) return ""

    val normalized = rawValue.trim()
    val countryCode = normalized.uppercase(Locale.ROOT)

    if (countryCode.length == 2 && countryCode.all { it.isLetter() }) {
        return getNationalitiesList().firstOrNull {
            it.startsWith(countryCode.toFlagEmoji())
        } ?: normalized
    }

    return normalized
}

/**
 * Retorna la llista de nacionalitats disponibles al formulari.
 *
 * @return col·lecció de nacionalitats ordenades per mostrar
 */
private fun getNationalitiesList(): List<String> {
    val countryCodes = listOf(
        "AD", "ES", "FR", "PT", "IT", "DE", "GB", "IE", "US", "CA",
        "MX", "AR", "BR", "CH", "BE", "NL", "AT", "GR", "PL", "RO"
    )

    return countryCodes.map { countryCode ->
        val locale = Locale.Builder()
            .setRegion(countryCode)
            .build()

        val flag = countryCode.toFlagEmoji()
        val systemName = locale.getDisplayCountry(Locale.getDefault())
        val nativeName = locale.getDisplayCountry(locale)

        if (systemName == nativeName) "$flag $systemName"
        else "$flag $systemName ($nativeName)"
    }.sortedBy { it.substring(3) }
}

/**
 * Converteix un codi de país a l'emoji de la seva bandera.
 *
 * @receiver codi ISO del país
 * @return emoji de bandera corresponent
 */
private fun String.toFlagEmoji(): String =
    uppercase(Locale.ROOT)
        .map { char -> Character.codePointAt("$char", 0) + 127397 }
        .joinToString(separator = "") { String(Character.toChars(it)) }