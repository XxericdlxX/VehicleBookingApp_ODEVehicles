package cat.copernic.odecoches.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cat.copernic.odecoches.R
import cat.copernic.odecoches.core.session.SessionState

/**
 * Mostra la pantalla de login i connecta les accions de la interfície amb el ViewModel.
 *
 * @param onLoginSuccess acció a executar quan el login es completa correctament
 * @param viewModel ViewModel associat a la pantalla
 * @param sessionState estat global de la sessió
 * @param onNavigateToRecoverPassword acció per navegar a la recuperació de contrasenya
 * @param onNavigateToRegister acció per navegar al registre
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    sessionState: SessionState,
    onNavigateToRecoverPassword: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedIn, sessionState) {
        if (uiState.isLoggedIn || sessionState is SessionState.Authenticated) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.email)) },
            singleLine = true,
            isError = uiState.emailError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        if (uiState.emailError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(uiState.emailError!!),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.password)) },
            singleLine = true,
            isError = uiState.passwordError != null,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        if (uiState.passwordError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(uiState.passwordError!!),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (uiState.generalError != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(uiState.generalError!!),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(stringResource(R.string.login))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onNavigateToRecoverPassword) {
            Text(stringResource(R.string.forgot_password))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(R.string.register_question))
            TextButton(onClick = onNavigateToRegister) {
                Text(text = stringResource(R.string.register))
            }
        }
    }
}