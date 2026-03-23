package cat.copernic.odecoches.data.repository

import cat.copernic.odecoches.data.remote.RetrofitClient
import cat.copernic.odecoches.data.remote.dto.ForgotPasswordRequest
import cat.copernic.odecoches.data.remote.dto.LoginRequest
import cat.copernic.odecoches.data.remote.dto.LoginResponse
import cat.copernic.odecoches.data.remote.dto.ResetPasswordRequest

/**
 * Repositori encarregat de les operacions d'autenticació contra el backend.
 */
class AuthRepository {

    /**
     * Intenta autenticar un usuari amb les credencials indicades.
     *
     * @param email correu electrònic de l'usuari
     * @param password contrasenya en text pla
     * @return resposta de login si la petició és correcta, o {@code null} en cas contrari
     */
    suspend fun login(email: String, password: String): LoginResponse? {
        val response = RetrofitClient.api.login(LoginRequest(email, password))
        return if (response.isSuccessful) response.body() else null
    }

    /**
     * Tanca la sessió associada a la capçalera Authorization indicada.
     *
     * @param authHeader capçalera Authorization completa
     * @return resposta HTTP del backend
     */
    suspend fun logout(authHeader: String) =
        RetrofitClient.api.logout(authHeader)

    /**
     * Sol·licita l'enviament d'un token de recuperació per correu electrònic.
     *
     * @param email correu electrònic de l'usuari
     * @return codi HTTP retornat pel backend
     */
    suspend fun forgotPassword(email: String): Int {
        val response = RetrofitClient.api.forgotPassword(ForgotPasswordRequest(email))
        return response.code()
    }

    /**
     * Restableix la contrasenya d'un usuari amb un token temporal.
     *
     * @param token token de recuperació
     * @param newPassword nova contrasenya
     * @return {@code true} si el backend confirma el canvi
     */
    suspend fun resetPassword(token: String, newPassword: String): Boolean {
        val response = RetrofitClient.api.resetPassword(ResetPasswordRequest(token, newPassword))
        return response.isSuccessful
    }
}