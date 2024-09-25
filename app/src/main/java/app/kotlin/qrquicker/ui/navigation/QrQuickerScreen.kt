package app.kotlin.qrquicker.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.kotlin.qrquicker.R
import app.kotlin.qrquicker.ui.components.NavigationBar
import app.kotlin.qrquicker.ui.components.NavigationBarItem
import app.kotlin.qrquicker.ui.screens.CreateQrScreen
import app.kotlin.qrquicker.ui.screens.DecodeImageScreen
import app.kotlin.qrquicker.ui.screens.ScanQrScreen
import app.kotlin.qrquicker.ui.styles.slideInFromLeft
import app.kotlin.qrquicker.ui.styles.slideInFromRight
import app.kotlin.qrquicker.ui.styles.slideOutFromLeft
import app.kotlin.qrquicker.ui.styles.slideOutFromRight

enum class Destination(
    val route: String,
    val tabIndex: Int
) {
    CREATE_QR(route = "create_qr", tabIndex = 0),
    SCAN_QR(route = "scan_qr", tabIndex = 1),
    DECODE_IMAGE(route = "decode_image", tabIndex = 2)
}

private val navigationBarItems: List<NavigationBarItem> = listOf(
    NavigationBarItem(
        icon = R.drawable.create_qr_code,
        iconAltText = "create qr code icon",
        label = R.string.navigation_bar_item_label_1,
        route = Destination.CREATE_QR.route
    ),
    NavigationBarItem(
        icon = R.drawable.scan_qr_code,
        iconAltText = "scan qr code icon",
        label = R.string.navigation_bar_item_label_2,
        route = Destination.SCAN_QR.route
    ),
    NavigationBarItem(
        icon = R.drawable.storage,
        iconAltText = "storage icon",
        label = R.string.navigation_bar_item_label_3,
        route = Destination.DECODE_IMAGE.route
    )
)

fun getEnterTransition(previousIndex: Int, selectedIndex: Int): EnterTransition {
    return if (previousIndex < selectedIndex) {
        slideInFromRight
    } else {
        slideInFromLeft
    }
}

fun getExitTransition(previousIndex: Int, selectedIndex: Int): ExitTransition {
    return if (previousIndex < selectedIndex) {
        slideOutFromRight
    } else {
        slideOutFromLeft
    }
}

private fun changeTab(
    route: String,
    navController: NavHostController,
    updateIndices: () -> Unit
) {
    updateIndices()
    navController.navigate(route) {
        popUpTo(id = 0) { inclusive = true }
    }
}

@Composable
fun QrQuickerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        val navController: NavHostController = rememberNavController()
        var selectedIndex: Int by remember {
            mutableIntStateOf(value = Destination.SCAN_QR.tabIndex)
        }
        var previousIndex: Int by remember {
            mutableIntStateOf(value = selectedIndex)
        }
        NavHost(
            navController = navController,
            startDestination = Destination.SCAN_QR.route,
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f)
        ) {
            composable(
                route = Destination.CREATE_QR.route,
                enterTransition = {
                    getEnterTransition(
                        previousIndex = previousIndex,
                        selectedIndex = selectedIndex
                    )
                },
                exitTransition = {
                    getExitTransition(
                        previousIndex = previousIndex,
                        selectedIndex = selectedIndex
                    )
                }
            ) {
                CreateQrScreen()
            }
            composable(
                route = Destination.SCAN_QR.route,
                enterTransition = {
                    getEnterTransition(
                        previousIndex = previousIndex,
                        selectedIndex = selectedIndex
                    )
                },
                exitTransition = {
                    getExitTransition(
                        previousIndex = previousIndex,
                        selectedIndex = selectedIndex
                    )
                }
            ) {
                ScanQrScreen()
            }
            composable(
                route = Destination.DECODE_IMAGE.route,
                enterTransition = {
                    getEnterTransition(
                        previousIndex = previousIndex,
                        selectedIndex = selectedIndex
                    )
                },
                exitTransition = {
                    getExitTransition(
                        previousIndex = previousIndex,
                        selectedIndex = selectedIndex
                    )
                }
            ) {
                DecodeImageScreen()
            }
        }

        NavigationBar(
            navigationBarItems = navigationBarItems,
            selectedIndex = selectedIndex,
            onTabPressedEvent = { newIndex, route ->
                changeTab(route, navController) {
                    previousIndex = selectedIndex
                    selectedIndex = newIndex
                }
            }
        )
    }
}

