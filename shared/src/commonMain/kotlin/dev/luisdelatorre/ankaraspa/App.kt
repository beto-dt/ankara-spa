package dev.luisdelatorre.ankaraspa

import AnkaraTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.luisdelatorre.ankaraspa.ui.Routes
import dev.luisdelatorre.ankaraspa.ui.screens.*
import androidx.savedstate.read


@Composable
fun App() {
    AnkaraTheme {
        val nav = rememberNavController()
        NavHost(navController = nav, startDestination = Routes.HOME) {
            composable(Routes.HOME) {
                HomeScreen(
                    onServiceClick = { nav.navigate(Routes.service(it)) },
                    onMyBookings = { nav.navigate(Routes.MY_BOOKINGS) },
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
        }
    }
}