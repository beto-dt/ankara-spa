package dev.luisdelatorre.ankaraspa

import AnkaraTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.read
import dev.luisdelatorre.ankaraspa.ui.Routes
import dev.luisdelatorre.ankaraspa.ui.screens.AdminScreen
import dev.luisdelatorre.ankaraspa.ui.screens.BookingScreen
import dev.luisdelatorre.ankaraspa.ui.screens.HomeScreen
import dev.luisdelatorre.ankaraspa.ui.screens.MyBookingsScreen
import dev.luisdelatorre.ankaraspa.ui.screens.ServiceDetailScreen
import dev.luisdelatorre.ankaraspa.ui.screens.SuccessScreen

@Composable
fun App() {
    AnkaraTheme {
        val nav = rememberNavController()
        NavHost(navController = nav, startDestination = Routes.HOME) {
            composable(Routes.HOME) {
                HomeScreen(
                    onServiceClick = { nav.navigate(Routes.service(it)) },
                    onMyBookings = { nav.navigate(Routes.MY_BOOKINGS) },
                    onAdmin = { nav.navigate(Routes.ADMIN) },
                )
            }
            composable(Routes.SERVICE) { back ->
                ServiceDetailScreen(
                    serviceId = back.arguments?.read { getStringOrNull("serviceId") } ?: "",
                    onBook = { nav.navigate(Routes.booking(it)) },
                    onBack = { nav.popBackStack() },
                )
            }
            composable(Routes.BOOKING) { back ->
                BookingScreen(
                    serviceId = back.arguments?.read { getStringOrNull("serviceId") } ?: "",
                    onSuccess = { code -> nav.navigate(Routes.success(code)) },
                    onBack = { nav.popBackStack() },
                )
            }
            composable(Routes.SUCCESS) { back ->
                SuccessScreen(
                    code = back.arguments?.read { getStringOrNull("code") } ?: "",
                    onDone = { nav.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
                )
            }
            composable(Routes.MY_BOOKINGS) {
                MyBookingsScreen(onBack = { nav.popBackStack() })
            }
            composable(Routes.ADMIN) {
                AdminScreen(onBack = { nav.popBackStack() })
            }
        }
    }
}