package cat.copernic.odecoches.ui.enregistrament

import android.net.Uri

/**
 * Classe de dades que representa l'estat de la interfície d'usuari (UI State) per a la pantalla de registre.
 * Aquesta classe centralitza tota la informació del formulari i l'estat de l'operació per garantir
 * una gestió reactiva de la interfície.
 *
 * @property nomComplet El nom complet de l'usuari.
 * @property numIdentificacio El número del document d'identitat (DNI/NIE/Passaport).
 * @property caducitatIdentificacio La data de caducitat del document d'identitat.
 * @property imatgeIdentificacio La URI de la imatge del document d'identitat seleccionada de la galeria.
 * @property tipusDocument El tipus de document d'identificació utilitzat.
 * @property tipusLlicencia El tipus de llicència de conduir de l'usuari.
 * @property caducitatLlicencia La data de caducitat de la llicència de conduir.
 * @property imatgeLlicencia La URI de la imatge de la llicència de conduir.
 * @property numTargeta El número de la targeta de pagament associada.
 * @property adreca L'adreça de residència de l'usuari.
 * @property nacionalitat La nacionalitat de l'usuari.
 * @property email L'adreça de correu electrònic per al compte.
 * @property password La contrasenya escollida per l'usuari.
 * @property isSuccess Indica si el procés de registre s'ha completat correctament.
 * @property isLoading Indica si hi ha una operació de càrrega en curs (per mostrar indicadors de progrés).
 * @property errorMessage L'identificador del recurs de cadena per al missatge d'error en cas de problemes amb el servidor.
 */
data class RegistreUIEstat(
    val nomComplet: String = "",
    val numIdentificacio: String = "",
    val caducitatIdentificacio: String = "",
    val imatgeIdentificacio: Uri? = null,
    val tipusDocument: String = "",
    val tipusLlicencia: String = "",
    val caducitatLlicencia: String = "",
    val imatgeLlicencia: Uri? = null,
    val numTargeta: String = "",
    val adreca: String = "",
    val nacionalitat: String = "",
    val email: String = "",
    val password: String = "",
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: Int? = null
)
