package cat.copernic.odecoches.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cat.copernic.odecoches.core.session.SessionManager
import cat.copernic.odecoches.core.session.SessionState
import cat.copernic.odecoches.data.remote.RetrofitClient
import cat.copernic.odecoches.data.repository.VehicleRepository
import cat.copernic.odecoches.features.reservations.data.repository.ReservaRepository
import cat.copernic.odecoches.features.reservations.presentation.screen.MyReservationsScreen
import cat.copernic.odecoches.features.reservations.presentation.screen.ReservaFlowRoot
import cat.copernic.odecoches.features.reservations.presentation.screen.ReservaStep3Screen
import cat.copernic.odecoches.features.reservations.presentation.screen.ReservationDetailScreen
import cat.copernic.odecoches.features.reservations.presentation.viewmodel.MyReservationsViewModel
import cat.copernic.odecoches.features.reservations.presentation.viewmodel.ReservaFlowViewModel
import cat.copernic.odecoches.features.reservations.presentation.viewmodel.ReservationDetailViewModel
import cat.copernic.odecoches.ui.ViewModelFactory
import cat.copernic.odecoches.ui.auth.LoginScreen
import cat.copernic.odecoches.ui.auth.LoginViewModel
import cat.copernic.odecoches.ui.auth.PasswordRecoveryScreen
import cat.copernic.odecoches.ui.auth.PasswordRecoveryViewModel
import cat.copernic.odecoches.ui.enregistrament.RegistreScreen
import cat.copernic.odecoches.ui.enregistrament.RegistreViewModel
import cat.copernic.odecoches.ui.profile.EditProfileScreen
import cat.copernic.odecoches.ui.profile.ProfileScreen
import cat.copernic.odecoches.ui.vehicles.VehicleDetailScreen
import cat.copernic.odecoches.ui.vehicles.VehicleListScreen
import cat.copernic.odecoches.ui.vehicles.VehicleViewModel
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: VehicleViewModel,
    loginViewModel: LoginViewModel,
    passwordRecoveryViewModel: PasswordRecoveryViewModel,
    reservaViewModel: ReservaFlowViewModel,
    myReservationsViewModel: MyReservationsViewModel,
    reservationDetailViewModel: ReservationDetailViewModel,
    sessionState: SessionState
) {
    val repository = VehicleRepository(RetrofitClient.api)

    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {

        composable(Routes.Home.route) {
            VehicleListScreen(
                viewModel = viewModel,
                onNavigateToDetail = { vehicle ->
                    viewModel.selectVehicle(vehicle)
                    navController.navigate(Routes.Detail.route)
                },
                onNavigateToReservaConfirm = { vehicle ->
                    val avail = viewModel.availability.value
                    reservaViewModel.setDates(avail.dataIniciText, avail.dataFiText)
                    reservaViewModel.seleccionarVehicle(vehicle)
                    navController.navigate(Routes.ReservaConfirm.route)
                },
                onRequireLogin = {
                    navController.navigate("login?redirect=home")
                }
            )
        }

        composable(Routes.Detail.route) {
            val vehicle by viewModel.selectedVehicle.collectAsState()

            vehicle?.let {
                VehicleDetailScreen(
                    vehicle = it,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToRegistre = {
                        navController.navigate(Routes.Register.route)
                    }
                )
            }
        }

        composable(Routes.Register.route) {
            val registerViewModel: RegistreViewModel = viewModel(
                factory = ViewModelFactory(
                    vehicleRepository = repository,
                    reservaRepository = ReservaRepository(RetrofitClient.api)
                )
            )

            RegistreScreen(
                viewModel = registerViewModel,
                onBack = { navController.popBackStack() },
                onRegistreSuccess = {
                    navController.navigate(Routes.Login.route)
                }
            )
        }

        composable(
            route = "login?redirect={redirect}",
            arguments = listOf(
                navArgument("redirect") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->

            val redirect = backStackEntry.arguments?.getString("redirect")

            LoginScreen(
                viewModel = loginViewModel,
                sessionState = sessionState,
                onNavigateToRecoverPassword = {
                    navController.navigate(Routes.PasswordRecovery.route)
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                },
                onLoginSuccess = {
                    if (redirect == "step3") {
                        navController.navigate(Routes.ReservaConfirm.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.PasswordRecovery.route) {
            PasswordRecoveryScreen(
                viewModel = passwordRecoveryViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Profile.route) {
            val userEmail = SessionManager.userEmail
            val isLogged = SessionManager.isLogged()

            if (!isLogged || userEmail.isNullOrBlank()) {
                LoginScreen(
                    viewModel = loginViewModel,
                    sessionState = sessionState,
                    onNavigateToRecoverPassword = {
                        navController.navigate(Routes.PasswordRecovery.route)
                    },
                    onNavigateToRegister = {
                        navController.navigate(Routes.Register.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Routes.Profile.route) {
                            popUpTo(Routes.Profile.route) { inclusive = true }
                        }
                    }
                )
            } else {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onEditProfile = {
                        navController.navigate(Routes.EditProfile.route)
                    },
                    onMyReservations = {
                        navController.navigate(Routes.MyReservations.route)
                    },
                    onLogout = {
                        loginViewModel.reset()
                        SessionManager.clear()

                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Profile.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Routes.ReservaFlow.route) {
            val userEmail = SessionManager.userEmail.orEmpty()

            ReservaFlowRoot(
                viewModel = reservaViewModel,
                vehicleViewModel = viewModel,
                userEmail = userEmail,
                navController = navController
            )
        }

        composable(Routes.EditProfile.route) {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onProfileUpdated = { navController.popBackStack() },
                onPasswordRecovery = {
                    navController.navigate(Routes.PasswordRecovery.route)
                }
            )
        }

        composable(Routes.MyReservations.route) {
            val userEmail = SessionManager.userEmail

            if (userEmail.isNullOrBlank()) {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.Profile.route) {
                        popUpTo(Routes.MyReservations.route) { inclusive = true }
                    }
                }
            } else {
                MyReservationsScreen(
                    userEmail = userEmail,
                    viewModel = myReservationsViewModel,
                    onBack = { navController.popBackStack() },
                    onReservationClick = { id ->
                        navController.navigate(Routes.ReservationDetail.createRoute(id))
                    }
                )
            }
        }

        composable(
            route = Routes.ReservationDetail.route,
            arguments = listOf(
                navArgument("id") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val email = SessionManager.userEmail.orEmpty()

            ReservationDetailScreen(
                reservationId = id,
                userEmail = email,
                viewModel = reservationDetailViewModel,
                onBack = { navController.popBackStack() },
                onCancelled = {
                    navController.popBackStack()
                    myReservationsViewModel.load(email)
                }
            )
        }

        composable(Routes.ReservaConfirm.route) {
            val userEmail = SessionManager.userEmail.orEmpty()
            val state by reservaViewModel.state.collectAsState()

            ReservaStep3Screen(
                state = state,
                userEmail = userEmail,
                onLoadPreview = {
                    reservaViewModel.carregarPreview()
                },
                onGoToLogin = {
                    navController.navigate("login?redirect=step3")
                },
                onConfirm = {
                    val email = SessionManager.userEmail

                    if (email.isNullOrBlank()) {
                        navController.navigate("login?redirect=step3")
                    } else {
                        reservaViewModel.confirmarReserva(email)
                    }
                },
                onGoHome = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.ReservaFlow.route) {
                            inclusive = true
                        }
                    }
                },
                onResetFlow = {
                    reservaViewModel.resetFlow()
                }
            )

            LaunchedEffect(state.reservaCreadaId) {
                if (state.reservaCreadaId != null) {
                    delay(1000)
                    viewModel.clearAvailability()
                    reservaViewModel.resetFlow()

                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = false }
                    }
                }
            }
        }
    }
}