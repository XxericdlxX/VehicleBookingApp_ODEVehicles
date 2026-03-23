package cat.copernic.odecoches.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cat.copernic.odecoches.R
import cat.copernic.odecoches.core.session.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Mostra la pantalla del flux de recuperació de contrasenya.
 *
 * @param onBack acció per tornar a la pantalla anterior
 * @param viewModel ViewModel associat a la pantalla
 */
@Composable
fun PasswordRecoveryScreen(
    viewModel: PasswordRecoveryViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val emailSessio = SessionManager.userEmail
    val loggedWithEmail = SessionManager.isLogged() && !emailSessio.isNullOrBlank()

    LaunchedEffect(loggedWithEmail, emailSessio) {
        viewModel.resetForEntry()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.password_recovery_title)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (state.step) {
                RecoveryStep.REQUEST_TOKEN -> {
                    if (loggedWithEmail) {
                        Text(
                            text = stringResource(R.string.password_recovery_send_to_registered_email),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = emailSessio,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.password_recovery_enter_email),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = state.email,
                            onValueChange = viewModel::onEmailChange,
                            label = { Text(stringResource(R.string.label_email)) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isLoading,
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = viewModel::requestToken,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(strokeWidth = 2.dp)
                            Spacer(Modifier.height(8.dp))
                        }
                        Text(stringResource(R.string.send_token_button))
                    }
                }

                RecoveryStep.RESET_PASSWORD -> {
                    Text(
                        text = stringResource(R.string.password_recovery_enter_token_and_password),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = state.token,
                        onValueChange = viewModel::onTokenChange,
                        label = { Text(stringResource(R.string.token_label)) },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.newPassword,
                        onValueChange = viewModel::onNewPasswordChange,
                        label = { Text(stringResource(R.string.new_password_label)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.repeatPassword,
                        onValueChange = viewModel::onRepeatPasswordChange,
                        label = { Text(stringResource(R.string.repeat_password_label)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading,
                        singleLine = true
                    )

                    Button(
                        onClick = viewModel::resetPassword,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(strokeWidth = 2.dp)
                            Spacer(Modifier.height(8.dp))
                        }
                        Text(stringResource(R.string.change_password_button))
                    }
                }
            }

            state.infoMessage?.let {
                Spacer(Modifier.height(4.dp))
                Text(it.asString(context), color = MaterialTheme.colorScheme.primary)
            }

            state.errorMessage?.let {
                Spacer(Modifier.height(4.dp))
                Text(it.asString(context), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}