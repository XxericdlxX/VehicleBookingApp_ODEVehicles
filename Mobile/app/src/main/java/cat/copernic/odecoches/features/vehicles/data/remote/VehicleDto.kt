package cat.copernic.odecoches.features.vehicles.data.remote

import com.google.gson.annotations.SerializedName

data class VehicleDto(

    @SerializedName("matricula")
    val matricula: String,

    @SerializedName("tipusVehicle")
    val tipusVehicle: String,

    @SerializedName("estatVehicle")
    val estatVehicle: String?,

    @SerializedName("preuHora")
    val preuHora: Double,

    @SerializedName("rutaDocumentacioPrivada")
    val rutaDocumentacioPrivada: String?,

    @SerializedName("color")
    val color: String,

    @SerializedName("potencia")
    val potencia: String
)