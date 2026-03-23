package cat.copernic.odecoches.data.repository

import cat.copernic.odecoches.data.remote.ApiService
import cat.copernic.odecoches.domain.model.RegistreRequest
import cat.copernic.odecoches.domain.model.Vehicle

/**
 * Repositori encarregat de gestionar les operacions de dades relacionades amb els vehicles.
 * Actua com a mediador entre la font de dades remota i la lògica de domini de l'aplicació.
 *
 * @property apiService El servei API utilitzat per realitzar les peticions de xarxa.
 */
class VehicleRepository(private val apiService: ApiService) {

    /**
     * Obté la llista completa de vehicles disponibles a través del servei API.
     *
     * @return Una llista d'objectes [Vehicle].
     */
    suspend fun getVehicles(): List<Vehicle> = apiService.getVehicles()

    /**
     * Realitza el registre d'un nou usuari al sistema.
     *
     * @param request L'objecte que conté la informació necessària per a la sol·licitud de registre.
     */
    suspend fun registrar(request: RegistreRequest) = apiService.registrarUsuari(request)

    /**
     * Cerca i retorna la informació d'un vehicle específic mitjançant la seva matrícula.
     *
     * @param matricula La matrícula del vehicle que es vol consultar.
     * @return L'objecte [Vehicle] corresponent a la matrícula proporcionada.
     */
    suspend fun getVehicleByMatricula(matricula: String): Vehicle {
        return apiService.obtenirVehiclePerMatricula(matricula)
    }
}
