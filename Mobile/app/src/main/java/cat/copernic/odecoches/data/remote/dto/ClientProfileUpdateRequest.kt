package cat.copernic.odecoches.data.remote.dto

/**
 * Model de petició utilitzat per actualitzar les dades del perfil del client.
 */
data class ClientProfileUpdateRequest(
    val nomComplet: String?,
    val dni: String,
    val dataCaducitatDocument: String?,
    val adreca: String?,
    val nacionalitat: String?,
    val carnetConduir: String?,
    val dataCaducitatCarnetConduir: String?,
    val numeroTargetaCredit: String?,
    val fotoPerfilBase64: String?,
    val docIdentitatBase64: String?,
    val docCarnetBase64: String?
)