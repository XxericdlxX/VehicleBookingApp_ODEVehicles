package cat.copernic.odecoches.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import cat.copernic.odecoches.core.session.SessionManager
import androidx.compose.ui.res.stringResource
import cat.copernic.odecoches.R

@Composable
fun BottomBar(navController: NavHostController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.nav_home)) },
            label = { Text(stringResource(R.string.nav_home)) },
            selected = currentRoute == Routes.Home.route,
            onClick = {
                navController.navigate(Routes.Home.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = stringResource(R.string.nav_profile)) },
            label = { Text(stringResource(R.string.nav_profile)) },
            selected = currentRoute == Routes.Profile.route ||
                    currentRoute == Routes.EditProfile.route ||
                    currentRoute == Routes.Login.route ||
                    currentRoute == Routes.PasswordRecovery.route,
            onClick = {
                navController.navigate(Routes.Profile.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.nav_reservations)) },
            label = { Text(stringResource(R.string.nav_reservations)) },
            selected = currentRoute == Routes.MyReservations.route,
            onClick = {
                if (!SessionManager.isLogged()) {
                    navController.navigate(Routes.Profile.route) {
                        launchSingleTop = true
                    }
                } else {
                    navController.navigate(Routes.MyReservations.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}
