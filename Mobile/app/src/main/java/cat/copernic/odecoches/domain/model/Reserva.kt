package cat.copernic.odecoches.domain.model

data class ReservaCreateRequest(
    val matricula: String,
    val dataInici: String,
    val dataFi: String
)

data class ReservaPreviewResponse(
    val importTotal: Double,
    val fianca: Double
)

data class ReservaResponse(
    val idReserva: Long
)