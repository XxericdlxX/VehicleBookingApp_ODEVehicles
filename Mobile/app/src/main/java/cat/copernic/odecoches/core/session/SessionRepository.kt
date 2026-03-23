package cat.copernic.odecoches.core.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val SESSION_DATASTORE_NAME = "session_preferences"

private val Context.dataStore by preferencesDataStore(name = SESSION_DATASTORE_NAME)

/**
 * Repositori encarregat de persistir la sessió de l'usuari amb DataStore.
 */
class SessionRepository(private val context: Context) {

    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    val sessionState: Flow<SessionState> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val token = preferences[Keys.TOKEN]

            if (!token.isNullOrBlank() && token != "session_token") {
                SessionState.Authenticated(token = token)
            } else {
                SessionState.Unauthenticated
            }
        }

    val userEmail: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[Keys.USER_EMAIL].orEmpty()
        }

    val token: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val token = preferences[Keys.TOKEN].orEmpty()
            if (token == "session_token") "" else token
        }

        /**
     * Desa el token i el correu electrònic de la sessió actual.
     *
     * @param token token de sessió
     * @param email correu electrònic de l'usuari
     */
    suspend fun saveSession(token: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.TOKEN] = token
            preferences[Keys.USER_EMAIL] = email
        }
    }

    /**
     * Elimina de DataStore les dades de la sessió activa.
     */
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(Keys.TOKEN)
            preferences.remove(Keys.USER_EMAIL)
        }
    }
}