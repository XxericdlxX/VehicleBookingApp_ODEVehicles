package cat.copernic.odecoches.features.reservations.data.remote
/**
 * Classe de dades que representa la resposta del servidor després
 * d'intentar anul·lar una reserva.
 *
 * Aquesta resposta indica si la reserva s'ha anul·lat correctament
 * i l'import que s'ha retornat al client.
 *
 * @property idReserva identificador de la reserva que s'ha intentat anul·lar
 * @property anulada indica si la reserva s'ha anul·lat correctament
 * @property importRetornat import que es retorna al client després de la cancel·lació
 * @property missatge missatge informatiu sobre el resultat de l'operació
 */
data class ReservaCancelResponse(
    val idReserva: Long? = null,
    val anulada: Boolean = false,
    val importRetornat: Double? = null,
    val missatge: String? = null
)