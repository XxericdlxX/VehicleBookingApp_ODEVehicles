package cat.copernic.odecoches.data.remote

import cat.copernic.odecoches.data.remote.dto.*
import cat.copernic.odecoches.data.remote.dto.LoginRequest
import cat.copernic.odecoches.data.remote.dto.LoginResponse
import cat.copernic.odecoches.domain.model.RegistreRequest
import cat.copernic.odecoches.domain.model.Vehicle
import cat.copernic.odecoches.features.reservations.data.remote.ReservaCreateRequest
import cat.copernic.odecoches.features.reservations.data.remote.ReservaResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.*


/**
 * Contracte Retrofit amb les operacions remotes utilitzades pels fluxos d'autenticació i perfil.
 */
interface ApiService {

    @GET("api/vehicles")
    suspend fun getVehicles(): List<Vehicle>

    @GET("api/vehicles/{matricula}")
    suspend fun obtenirVehiclePerMatricula(@Path("matricula") matricula: String): Vehicle

    @POST("api/registre")
    suspend fun registrarUsuari(@Body request: RegistreRequest): Response<ResponseBody>

    /**
     * Inicia sessió amb email i contrasenya.
     */
    @POST("api/reserves/preview")
    suspend fun previewReserva(
        @Body body: ReservaCreateRequest
    ): ReservaResponse

    @POST("api/reserves")
    suspend fun crearReserva(
        @Header("X-User") userEmail: String,
        @Body body: ReservaCreateRequest
    ): ReservaResponse

    @GET("api/vehicles/disponibles")
    suspend fun vehiclesDisponibles(
        @Query("dataInici") dataInici: String,
        @Query("dataFi") dataFi: String
    ): List<Vehicle>
    
    /**
     * Envia la petició de login al backend.
     *
     * @param request credencials de l'usuari
     * @return resposta HTTP amb el token i el rol si el login és correcte
     */
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    /**
     * Tanca la sessió del token indicat.
     *
     * @param token capçalera Authorization amb el token de sessió
     * @return resposta HTTP del procés de logout
     */
    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Unit>

    /**
     * Recupera el perfil del client autenticat.
     *
     * @param token capçalera Authorization amb el token actiu
     * @return resposta HTTP amb les dades del perfil
     */
    @GET("api/client/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ClientProfileResponse>

    /**
     * Actualitza el perfil del client autenticat.
     *
     * @param token capçalera Authorization amb el token actiu
     * @param request dades del perfil a actualitzar
     * @return resposta HTTP amb el perfil actualitzat
     */
    @PUT("api/client/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: ClientProfileUpdateRequest
    ): Response<ClientProfileResponse>

    /**
     * Inicia la recuperació de contrasenya d'un usuari.
     *
     * @param request correu electrònic de l'usuari
     * @return resposta HTTP del procés d'enviament del token
     */
    @POST("api/auth/password/forgot")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<Unit>

    /**
     * Restableix la contrasenya a partir d'un token temporal.
     *
     * @param request token de recuperació i nova contrasenya
     * @return resposta HTTP del restabliment
     */
    @POST("api/auth/password/reset")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<Unit>

    @GET("api/reserves/me")
    suspend fun getMyReservations(
        @Header("X-User") userEmail: String,
        @Query("order") order: String = "desc"
    ): List<ReservaResponse>


    @GET("api/reserves/{id}")
    suspend fun getReservationDetail(
        @Path("id") id: Long,
        @Header("X-User") userEmail: String
    ): cat.copernic.odecoches.features.reservations.data.remote.ReservaDetallResponse

    @DELETE("api/reserves/{id}")
    suspend fun cancelReservation(
        @Path("id") id: Long,
        @Header("X-User") userEmail: String
    ): cat.copernic.odecoches.features.reservations.data.remote.ReservaCancelResponse
}
