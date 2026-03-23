package cat.copernic.odecoches.ui.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.odecoches.R
import cat.copernic.odecoches.core.session.SessionManager
import cat.copernic.odecoches.data.remote.dto.ClientProfileResponse

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Mostra la pantalla principal del perfil del client.
 *
 * @param onBack acció per tornar enrere
 * @param onEditProfile acció per navegar a l'edició del perfil
 * @param onMyReservations acció per navegar a les reserves del client
 * @param onLogout acció a executar quan el logout es completa correctament
 * @param viewModel ViewModel associat a la pantalla
 */
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onMyReservations: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val showLogoutConfirm = rememberSaveable { mutableStateOf(false) }
    val isLoggingOut = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (SessionManager.isLogged()) {
            viewModel.loadProfile()
        }
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && SessionManager.isLogged()) {
                viewModel.loadProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.profile == null -> {
                    Text(
                        text = state.error?.asString(context)
                            ?: stringResource(R.string.profile_no_data),
                        color = if (state.error != null) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                else -> {
                    ProfileContent(
                        profile = state.profile!!,
                        onEditProfile = onEditProfile,
                        onMyReservations = onMyReservations,
                        isBusy = isLoggingOut.value,
                        onRequestLogout = { showLogoutConfirm.value = true }
                    )
                }
            }
        }
    }

    if (showLogoutConfirm.value) {
        AlertDialog(
            onDismissRequest = { if (!isLoggingOut.value) showLogoutConfirm.value = false },
            title = { Text(stringResource(R.string.logout_title)) },
            text = { Text(stringResource(R.string.logout_confirm_message)) },
            confirmButton = {
                TextButton(
                    enabled = !isLoggingOut.value,
                    onClick = {
                        isLoggingOut.value = true
                        viewModel.logout {
                            isLoggingOut.value = false
                            showLogoutConfirm.value = false
                            onLogout()
                        }
                    }
                ) {
                    Text(
                        if (isLoggingOut.value) {
                            stringResource(R.string.logging_out)
                        } else {
                            stringResource(R.string.yes)
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isLoggingOut.value,
                    onClick = { showLogoutConfirm.value = false }
                ) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }
}

/**
 * Renderitza el contingut visual de la pantalla de perfil.
 */
@Composable
private fun ProfileContent(
    profile: ClientProfileResponse,
    onEditProfile: () -> Unit,
    onMyReservations: () -> Unit,
    isBusy: Boolean,
    onRequestLogout: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showDoc by remember { mutableStateOf<DocType?>(null) }

    val fotoBitmap = remember(profile.fotoPerfilBase64) {
        decodeBase64ToBitmap(profile.fotoPerfilBase64)
    }
    val dniBitmap = remember(profile.docIdentitatBase64) {
        decodeBase64ToBitmap(profile.docIdentitatBase64)
    }
    val carnetBitmap = remember(profile.docCarnetBase64) {
        decodeBase64ToBitmap(profile.docCarnetBase64)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_your_data_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.profile_your_data_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (fotoBitmap != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = fotoBitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.profile_photo_content_description),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                            .aspectRatio(fotoBitmap.width.toFloat() / fotoBitmap.height.toFloat())
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = { if (dniBitmap != null) showDoc = DocType.DNI },
                enabled = dniBitmap != null,
                label = {
                    Text(
                        if (dniBitmap != null) {
                            stringResource(R.string.profile_dni_uploaded_view)
                        } else {
                            stringResource(R.string.profile_dni_not_uploaded)
                        }
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (dniBitmap != null) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null
                    )
                }
            )

            AssistChip(
                onClick = { if (carnetBitmap != null) showDoc = DocType.CARNET },
                enabled = carnetBitmap != null,
                label = {
                    Text(
                        if (carnetBitmap != null) {
                            stringResource(R.string.profile_license_uploaded_view)
                        } else {
                            stringResource(R.string.profile_license_not_uploaded)
                        }
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (carnetBitmap != null) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null
                    )
                }
            )
        }

        if (showDoc == DocType.DNI && dniBitmap != null) {
            DocumentViewerDialog(
                title = stringResource(R.string.profile_identity_document_title),
                bitmap = dniBitmap,
                onDismiss = { showDoc = null }
            )
        }

        if (showDoc == DocType.CARNET && carnetBitmap != null) {
            DocumentViewerDialog(
                title = stringResource(R.string.profile_driver_license_title),
                bitmap = carnetBitmap,
                onDismiss = { showDoc = null }
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                ProfileInfoRow(
                    label = stringResource(R.string.full_name_label),
                    value = profile.nomComplet.orEmpty(),
                    icon = Icons.Default.Badge
                )
                ProfileDivider()

                ProfileInfoRow(
                    label = stringResource(R.string.email_label),
                    value = profile.username,
                    icon = Icons.Default.Email
                )
                ProfileDivider()

                ProfileInfoRow(
                    label = stringResource(R.string.document_id_short_label),
                    value = profile.dni,
                    icon = Icons.Default.Fingerprint
                )
                ProfileDivider()

                ProfileInfoRow(
                    label = stringResource(R.string.document_expiry_full_label),
                    value = profile.dataCaducitatDocument.orEmpty(),
                    icon = Icons.Default.Event
                )
                ProfileDivider()

                ProfileInfoRow(
                    label = stringResource(R.string.license_short_label),
                    value = profile.carnetConduir.orEmpty(),
                    icon = Icons.Default.DirectionsCar
                )
                ProfileDivider()

                ProfileInfoRow(
                    label = stringResource(R.string.license_expiry_short_label),
                    value = profile.dataCaducitatCarnetConduir.orEmpty(),
                    icon = Icons.Default.Event
                )
                ProfileDivider()

                ProfileInfoRow(
                    label = stringResource(R.string.address_label),
                    value = profile.adreca.orEmpty(),
                    icon = Icons.Default.Home
                )
                ProfileDivider()

                ProfileInfoRow(
                    label = stringResource(R.string.nationality_label),
                    value = profile.nacionalitat.orEmpty(),
                    icon = Icons.Default.Public
                )
                ProfileDivider()

                ProfileInfoRow(
                    label = stringResource(R.string.credit_card_label),
                    value = profile.numeroTargetaCredit.orEmpty(),
                    icon = Icons.Default.CreditCard
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onEditProfile,
                modifier = Modifier.weight(1f),
                enabled = !isBusy
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.edit_profile_button))
            }

            Button(
                onClick = onMyReservations,
                modifier = Modifier.weight(1f),
                enabled = !isBusy
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.view_reservations_button))
            }
        }

        Button(
            onClick = onRequestLogout,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isBusy
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                if (isBusy) {
                    stringResource(R.string.logging_out_full)
                } else {
                    stringResource(R.string.logout_button)
                }
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}

/**
 * Mostra una fila informativa amb una dada del perfil.
 */
@Composable
private fun ProfileInfoRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value.ifBlank { "-" },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Dibuixa un separador visual entre seccions del perfil.
 */
@Composable
private fun ProfileDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

private enum class DocType {
    DNI, CARNET
}

/**
 * Mostra un diàleg amb la visualització d'un document codificat en Base64.
 */
@Composable
private fun DocumentViewerDialog(
    title: String,
    bitmap: Bitmap,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        },
        title = { Text(title) },
        text = {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 520.dp),
                contentScale = ContentScale.Fit
            )
        }
    )
}

/**
 * Decodifica una cadena Base64 a un objecte [Bitmap].
 *
 * @param base64 contingut de la imatge en Base64
 * @return bitmap resultant o {@code null} si la conversió falla
 */
private fun decodeBase64ToBitmap(base64: String?): Bitmap? {
    if (base64.isNullOrBlank()) return null
    return try {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) {
        null
    }
}