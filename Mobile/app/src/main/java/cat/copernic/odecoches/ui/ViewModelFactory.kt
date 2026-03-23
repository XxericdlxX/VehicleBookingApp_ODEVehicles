package cat.copernic.odecoches.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cat.copernic.odecoches.data.repository.VehicleRepository
import cat.copernic.odecoches.features.reservations.data.repository.ReservaRepository
import cat.copernic.odecoches.ui.enregistrament.RegistreViewModel
import cat.copernic.odecoches.ui.vehicles.VehicleViewModel

/**
 * Factoria personalitzada per a la creació d'instàncies de ViewModel.
 * Permet la injecció de dependències (repositoris) en els ViewModels de l'aplicació.
 *
 * @property vehicleRepository El repositori de vehicles necessari per als ViewModels.
 * @property reservaRepository El repositori de reserves necessari per gestionar lloguers.
 */
class ViewModelFactory(
    private val vehicleRepository: VehicleRepository,
    private val reservaRepository: ReservaRepository
) : ViewModelProvider.Factory {

    /**
     * Crea una nova instància de la classe ViewModel especificada.
     *
     * @param modelClass La classe del ViewModel que es vol instanciar.
     * @return Una instància del ViewModel configurada amb els repositoris corresponents.
     * @throws IllegalArgumentException Si la classe del ViewModel no és reconeguda per aquesta factoria.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Comprova si el ViewModel demanat és VehicleViewModel
        if (modelClass.isAssignableFrom(VehicleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VehicleViewModel(vehicleRepository, reservaRepository) as T
        }

        // Comprova si el ViewModel demanat és RegistreViewModel
        if (modelClass.isAssignableFrom(RegistreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistreViewModel(vehicleRepository) as T
        }

        // Llança una excepció si s'intenta crear un ViewModel no definit aquí
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
