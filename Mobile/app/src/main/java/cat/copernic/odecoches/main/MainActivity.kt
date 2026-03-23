package cat.copernic.odecoches.main

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cat.copernic.odecoches.core.session.SessionManager
import cat.copernic.odecoches.core.session.SessionRepository
import cat.copernic.odecoches.core.session.SessionState
import cat.copernic.odecoches.data.remote.RetrofitClient
import cat.copernic.odecoches.data.repository.AuthRepository
import cat.copernic.odecoches.data.repository.VehicleRepository
import cat.copernic.odecoches.features.reservations.data.repository.ReservaRepository
import cat.copernic.odecoches.features.reservations.presentation.viewmodel.MyReservationsViewModel
import cat.copernic.odecoches.features.reservations.presentation.viewmodel.ReservaFlowViewModel
import cat.copernic.odecoches.features.reservations.presentation.viewmodel.ReservationDetailViewModel
import cat.copernic.odecoches.navigation.BottomBar
import cat.copernic.odecoches.navigation.NavGraph
import cat.copernic.odecoches.navigation.Routes
import cat.copernic.odecoches.ui.auth.LoginViewModel
import cat.copernic.odecoches.ui.auth.PasswordRecoveryViewModel
import cat.copernic.odecoches.ui.theme.ODECochesTheme
import cat.copernic.odecoches.ui.vehicles.VehicleViewModel

class MainActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(SessionRepository(applicationContext))

        val vehicleRepository = VehicleRepository(RetrofitClient.api)
        val reservaRepository = ReservaRepository(api = RetrofitClient.api)
        val vehicleViewModel = VehicleViewModel(vehicleRepository, reservaRepository)

        val authRepository = AuthRepository()
        val passwordRecoveryViewModel = PasswordRecoveryViewModel(authRepository)
        val loginViewModel = LoginViewModel(authRepository, sessionManager)

        val reservaViewModel = ReservaFlowViewModel(reservaRepository)
        val myReservationsViewModel = MyReservationsViewModel(
            ReservaRepository(RetrofitClient.api)
        )
        val reservationDetailViewModel = ReservationDetailViewModel(reservaRepository)

        setContent {
            ODECochesTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                val sessionState by sessionManager.sessionState.collectAsState(
                    initial = SessionState.Loading
                )

                val showBottomBar = sessionState != SessionState.Loading && when (currentRoute) {
                    Routes.Home.route,
                    Routes.Profile.route,
                    Routes.MyReservations.route,
                    Routes.ReservaConfirm.route,
                    Routes.Login.route,
                    Routes.PasswordRecovery.route -> true
                    else -> false
                }

                Scaffold(
                    bottomBar = { if (showBottomBar) BottomBar(navController) }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (sessionState == SessionState.Loading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            NavGraph(
                                navController = navController,
                                viewModel = vehicleViewModel,
                                loginViewModel = loginViewModel,
                                passwordRecoveryViewModel = passwordRecoveryViewModel,
                                reservaViewModel = reservaViewModel,
                                myReservationsViewModel = myReservationsViewModel,
                                reservationDetailViewModel = reservationDetailViewModel,
                                sessionState = sessionState
                            )
                        }
                    }
                }
            }
        }
    }
}