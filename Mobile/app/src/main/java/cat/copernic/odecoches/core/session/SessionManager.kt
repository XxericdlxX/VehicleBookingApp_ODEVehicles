package cat.copernic.odecoches.core.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Gestiona l'estat de sessió en memòria i el sincronitza amb el repositori persistent.
 */
class SessionManager(
    private val sessionRepository: SessionRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState: StateFlow<SessionState> = _sessionState

    private val _userEmail = MutableStateFlow("")

    private val _token = MutableStateFlow("")

    init {
        instance = this

        scope.launch {
            sessionRepository.sessionState.collect {
                _sessionState.value = it
            }
        }

        scope.launch {
            sessionRepository.userEmail.collect {
                _userEmail.value = it
            }
        }

        scope.launch {
            sessionRepository.token.collect {
                _token.value = it
            }
        }
    }

    /**
     * Desa una sessió autenticada tant en memòria com al repositori persistent.
     *
     * @param token token de sessió retornat pel backend
     * @param email correu electrònic de l'usuari autenticat
     */
    fun saveSession(token: String, email: String) {
        _token.value = token
        _userEmail.value = email
        _sessionState.value = SessionState.Authenticated(token = token)

        scope.launch {
            sessionRepository.saveSession(token, email)
        }
    }

    /**
     * Elimina la sessió actual de memòria i del repositori persistent.
     */
    fun clearSession() {
        _token.value = ""
        _userEmail.value = ""
        _sessionState.value = SessionState.Unauthenticated

        scope.launch {
            sessionRepository.clearSession()
        }
    }

    companion object {
        private var instance: SessionManager? = null

        val userEmail: String?
            get() = instance?._userEmail?.value?.takeIf { it.isNotBlank() }

        val token: String?
            get() = instance?._token?.value?.takeIf {
                it.isNotBlank() && it != "session_token"
            }

        /**
         * Indica si hi ha una sessió autenticada activa.
         *
         * @return {@code true} si l'usuari està autenticat
         */
        fun isLogged(): Boolean {
            return instance?._sessionState?.value is SessionState.Authenticated
        }

        /**
         * Tanca la sessió global actual.
         */
        fun clear() {
            instance?.clearSession()
        }
    }
}