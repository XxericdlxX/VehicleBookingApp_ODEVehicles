package cat.copernic.odecoches.core.session

/**
 * Representa els possibles estats de la sessió de l'usuari.
 */
sealed interface SessionState {
        /** Estat inicial mentre es carrega la sessió persistida. */
    data object Loading : SessionState
        /** Estat que indica que no hi ha cap usuari autenticat. */
    data object Unauthenticated : SessionState
        /** Estat que indica que la sessió és vàlida i conté un token actiu. */
    data class Authenticated(
        val token: String
    ) : SessionState
}