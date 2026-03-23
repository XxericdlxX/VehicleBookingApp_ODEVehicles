package cat.copernic.odecoches.features.reservations.data.remote
/**
 * Classe de dades que representa el detall complet d'una reserva retornat pel backend.
 *
 * Aquesta resposta s'utilitza quan l'usuari consulta el detall d'una reserva
 * concreta des de l'aplicació mòbil. Inclou informació tant de la reserva
 * com del vehicle associat.
 *
 * Informació de la reserva:
 * - identificador de la reserva
 * - correu electrònic del client
 * - dates d'inici i finalització
 * - import total de la reserva
 * - fiança associada
 *
 * Informació del vehicle:
 * - matrícula
 * - tipus de vehicle
 * - motor
 * - potència
 * - color
 * - preu per hora
 * - fotografia del vehicle
 *
 * @property idReserva identificador de la reserva
 * @property clientEmail correu electrònic del client que ha fet la reserva
 * @property dataInici data d'inici de la reserva
 * @property dataFi data de finalització de la reserva
 * @property importTotal import total de la reserva
 * @property fianca import de la fiança
 * @property vehicleMatricula matrícula del vehicle reservat
 * @property vehicleTipus tipus de vehicle
 * @property vehicleMotor tipus de motor del vehicle
 * @property vehiclePotencia potència del vehicle
 * @property vehicleColor color del vehicle
 * @property vehiclePreuHora preu per hora del vehicle
 * @property vehicleFoto ruta o identificador de la fotografia del vehicle
 */
data class ReservaDetallResponse(
    val idReserva: Long? = null,
    val clientEmail: String? = null,
    val dataInici: String? = null,
    val dataFi: String? = null,
    val importTotal: Double? = null,
    val fianca: Double? = null,

    val vehicleMatricula: String? = null,
    val vehicleTipus: String? = null,
    val vehicleMotor: String? = null,
    val vehiclePotencia: Int? = null,
    val vehicleColor: String? = null,
    val vehiclePreuHora: Double? = null,
    val vehicleFoto: String? = null
)