package cat.copernic.odecoches.domain.model

data class Vehicle(
    val matricula: String,
    val tipusVehicle: String,
    val estatVehicle: String?,
    val motor: String?,
    val potencia: String?,
    val color: String?,
    val limitQuilometratge: Int?,
    val preuHora: Double,
    val fiancaEstandard: Double,
    val minDiesLloguer: Int?,
    val maxDiesLloguer: Int?,
    val rutaDocumentacioPrivada: String?)
