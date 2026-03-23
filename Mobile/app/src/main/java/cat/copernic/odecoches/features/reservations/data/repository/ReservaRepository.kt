package cat.copernic.odecoches.features.reservations.data.repository

import cat.copernic.odecoches.data.remote.ApiService
import cat.copernic.odecoches.domain.model.Vehicle
import cat.copernic.odecoches.features.reservations.data.remote.ReservaCreateRequest
import cat.copernic.odecoches.features.reservations.data.remote.ReservaResponse
/**
 * Repositori encarregat de gestionar les operacions relacionades amb les reserves.
 *
 * Aquesta classe actua com a intermediari entre la capa de dades remotes
 * (API REST) i la resta de l'aplicació. S'encarrega de cridar els endpoints
 * del servei {@link ApiService} per obtenir o modificar informació
 * relacionada amb les reserves i els vehicles disponibles.
 *
 * @property api servei d'API utilitzat per realitzar les peticions al backend
 */
class ReservaRepository(private val api: ApiService) {
    /**
     * Obté la llista de vehicles disponibles per a un rang de dates determinat.
     *
     * @param dataInici data d'inici de la reserva en format "YYYY-MM-DD"
     * @param dataFi data de finalització de la reserva en format "YYYY-MM-DD"
     *
     * @return llista de vehicles disponibles
     */
    suspend fun vehiclesDisponibles(dataInici: String, dataFi: String): List<Vehicle> {
        return api.vehiclesDisponibles(dataInici, dataFi)
    }
    /**
     * Calcula una previsualització d'una reserva abans de crear-la.
     *
     * Permet conèixer l'import total i la fiança sense guardar
     * la reserva al sistema.
     *
     * @param req dades de la reserva que es vol calcular
     * @return resposta amb la informació calculada de la reserva
     */
    suspend fun previewReserva(req: ReservaCreateRequest): ReservaResponse {
        return api.previewReserva(req)
    }
    /**
     * Crea una nova reserva per a un vehicle.
     *
     * @param userEmail correu electrònic de l'usuari que crea la reserva
     * @param body dades de la reserva a crear
     *
     * @return reserva creada
     */
    suspend fun crearReserva(userEmail: String, body: ReservaCreateRequest): ReservaResponse{
        return api.crearReserva(userEmail, body)
    }
    /**
     * Obté totes les reserves de l'usuari autenticat.
     *
     * @param userEmail correu electrònic de l'usuari
     * @param order ordre de classificació de les reserves (asc o desc)
     *
     * @return llista de reserves de l'usuari
     */
    suspend fun getMyReservations(userEmail: String, order: String = "desc") =
        api.getMyReservations(userEmail, order)
    /**
     * Obté el detall d'una reserva concreta.
     *
     * @param id identificador de la reserva
     * @param userEmail correu electrònic de l'usuari
     *
     * @return informació detallada de la reserva
     */
    suspend fun getReservationDetail(id: Long, userEmail: String) =
        api.getReservationDetail(id, userEmail)
    /**
     * Anul·la una reserva existent.
     *
     * @param id identificador de la reserva
     * @param userEmail correu electrònic de l'usuari
     *
     * @return resposta amb el resultat de la cancel·lació
     */
    suspend fun cancelReservation(id: Long, userEmail: String) =
        api.cancelReservation(id, userEmail)
}