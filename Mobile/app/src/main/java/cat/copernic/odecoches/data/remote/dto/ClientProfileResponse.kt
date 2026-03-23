package cat.copernic.odecoches.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Model de resposta amb les dades del perfil del client retornades pel backend.
 */
data class ClientProfileResponse(
    val nomComplet: String? = null,
    val dni: String,
    val dataCaducitatDocument: String? = null,
    @SerializedName("email")
    val username: String,
    val adreca: String?,
    val nacionalitat: String? = null,
    val carnetConduir: String?,
    val dataCaducitatCarnetConduir: String? = null,
    val numeroTargetaCredit: String?,
    val fotoPerfilBase64: String?,
    val docIdentitatBase64: String? = null,
    val docCarnetBase64: String? = null
)