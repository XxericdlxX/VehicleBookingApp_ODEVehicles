package cat.copernic.odecoches.features.reservations.data.remote
/**
 * Classe de dades que representa la petició de creació d'una reserva.
 *
 * Aquest objecte s'envia des de l'aplicació mòbil al backend per crear
 * una nova reserva d'un vehicle en un període de dates determinat.
 *
 * Les dates es representen en format ISO: "YYYY-MM-DD".
 *
 * @property matricula matrícula del vehicle que es vol reservar
 * @property dataInici data d'inici de la reserva en format "YYYY-MM-DD"
 * @property dataFi data de finalització de la reserva en format "YYYY-MM-DD"
 */
data class ReservaCreateRequest(
    val matricula: String,
    val dataInici: String, // "YYYY-MM-DD"
    val dataFi: String     // "YYYY-MM-DD"
)