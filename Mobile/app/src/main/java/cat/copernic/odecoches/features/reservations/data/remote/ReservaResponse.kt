package cat.copernic.odecoches.features.reservations.data.remote

import com.google.gson.annotations.SerializedName
/**
 * Classe de dades que representa la resposta bàsica d'una reserva
 * retornada pel backend.
 *
 * Aquesta resposta s'utilitza principalment quan es creen o es llisten
 * reserves des de l'aplicació mòbil. Conté la informació essencial
 * de la reserva i del vehicle associat.
 *
 * @property idReserva identificador únic de la reserva
 * @property vehicleMatricula matrícula del vehicle reservat
 * @property dataInici data d'inici de la reserva en format "YYYY-MM-DD"
 * @property dataFi data de finalització de la reserva en format "YYYY-MM-DD"
 * @property importTotal import total de la reserva
 * @property fiancaPagada import de la fiança pagada per la reserva
 */
data class ReservaResponse(
    val idReserva: Long,
    val vehicleMatricula: String?,
    val dataInici: String?,
    val dataFi: String?,
    val importTotal: Double?,
    @SerializedName("fianca") val fiancaPagada: Double?
)