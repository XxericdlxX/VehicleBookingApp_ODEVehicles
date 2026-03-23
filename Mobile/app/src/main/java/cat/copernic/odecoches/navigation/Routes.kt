package cat.copernic.odecoches.navigation


// --- CLASSE DE RUTES (Routes.kt) ---
// Aquesta classe segellada defineix de manera centralitzada totes les destinacions de l'aplicació.
// L'objectiu és evitar l'ús de cadenes de text (Strings) escrites a mà per prevenir errors de tipografia.
// Per afegir una nova pantalla, s'ha de crear un nou 'object' amb la seva ruta corresponent.
// COM AFEGIR UNA NOVA RUTA:
// 1. Crea un nou 'object' dins de la classe 'Routes' seguint el format:
//    object NomDestinacio : Routes("identificador_textual")
//    Exemple: object Perfil : Routes("profile")
sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Detail : Routes("detail")
    object Register : Routes("register")
    object Login : Routes("login")
    object PasswordRecovery : Routes("password_recovery")
    object Profile : Routes("profile")
    object EditProfile : Routes("edit_profile")
    object ReservaFlow : Routes("reserva_flow")
    object ReservaConfirm : Routes("reserva_confirm")

    object MyReservations : Routes("my_reservations")

    object ReservationDetail : Routes("reservation_detail/{id}") {
        fun createRoute(id: Long) = "reservation_detail/$id"
    }
}