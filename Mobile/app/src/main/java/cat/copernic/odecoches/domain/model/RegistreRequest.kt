package cat.copernic.odecoches.domain.model

import com.google.gson.annotations.SerializedName

data class RegistreRequest(
    @SerializedName("nom_complet")
    val nomComplet: String,

    val dni: String,

    @SerializedName("data_caducitat_document")
    val caducitatDni: String,

    val nacionalitat: String,

    val adreca: String,

    @SerializedName("numero_targeta_credit")
    val numTargeta: String,

    val email: String,

    val password: String,

    @SerializedName("carnet_conduir")
    val tipusLlicencia: String,

    @SerializedName("data_caducitat_carnet_conduir")
    val caducitatLlicencia: String,

    // ✅ ESTO ES LO QUE TE FALTA PARA QUE SE QUITE EL ROJO:
    @SerializedName("doc_identitat_base64")
    val docIdentitatBase64: String? = null,

    @SerializedName("doc_carnet_base64")
    val docCarnetBase64: String? = null
)
