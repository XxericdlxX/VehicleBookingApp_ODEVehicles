package cat.copernic.odecoches.data.repository

import cat.copernic.odecoches.data.remote.RetrofitClient
import cat.copernic.odecoches.data.remote.dto.ClientProfileUpdateRequest

/**
 * Repositori encarregat de consultar i actualitzar el perfil del client.
 */
class ClientRepository {

    /**
     * Recupera el perfil del client autenticat.
     *
     * @param authHeader capçalera Authorization completa
     * @return resposta HTTP amb les dades del perfil
     */
    suspend fun getProfile(authHeader: String) =
        RetrofitClient.api.getProfile(authHeader)

    /**
     * Actualitza el perfil del client autenticat.
     *
     * @param authHeader capçalera Authorization completa
     * @param req dades del perfil a desar
     * @return resposta HTTP amb el perfil actualitzat
     */
    suspend fun updateProfile(authHeader: String, req: ClientProfileUpdateRequest) =
        RetrofitClient.api.updateProfile(authHeader, req)
}
