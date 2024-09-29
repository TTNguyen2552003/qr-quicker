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

/**
 * Enum class representing different app destinations, each with a specific route and tab index.
 * @param route for the destination
 * @param tabIndex associated with the tab in the bottom navigation
 */
enum class Destination(
    val route: String,
    val tabIndex: Int
) {
    CREATE_QR(route = "create_qr", tabIndex = 0),
    SCAN_QR(route = "scan_qr", tabIndex = 1),
    DECODE_IMAGE(route = "decode_image", tabIndex = 2)
}

// List representing the items in the bottom navigation bar, each linked to a destination route.
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

// Function to get the enter transition when navigating to a new tab.
fun getEnterTransition(previousIndex: Int, selectedIndex: Int): EnterTransition {
    return if (previousIndex < selectedIndex) {
        slideInFromRight
    } else {
        slideInFromLeft
    }
}

// Function to get the exit transition when leaving a tab.
fun getExitTransition(previousIndex: Int, selectedIndex: Int): ExitTransition {
    return if (previousIndex < selectedIndex) {
        slideOutFromRight   // Transition animation for sliding out to the right
    } else {
        slideOutFromLeft    // Transition animation for sliding out to the left
    }
}

// Function to handle tab change navigation.
// Updates the tab index and navigates to the specified route using the navController.
/**
 * @param route is the route of destination to navigate to
 * @param navController is the navigation controller responsible for managing navigation
 * @param updateIndices is the lambda function to update the selected and previous indices
 */
private fun changeTab(
    route: String,
    navController: NavHostController,
    updateIndices: () -> Unit
) {
    updateIndices()
    navController.navigate(route) {
//         Remove all previous destinations from the back stack
        popUpTo(id = 0) { inclusive = true }
    }
}

// Main Composable function representing the QrQuicker screen layout and navigation structure.
@Composable
fun QrQuickerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
//         NavController to handle navigation
        val navController: NavHostController =
            rememberNavController()
//         Initialize selectedIndex to "Scan QR" tab
        var selectedIndex: Int by remember {
            mutableIntStateOf(value = Destination.SCAN_QR.tabIndex)
        }
//         Keep track of the previous selected index
        var previousIndex: Int by remember {
            mutableIntStateOf(value = selectedIndex)
        }

//         Navigation host that manages the composable screens for each route
        NavHost(
            navController = navController,
            startDestination = Destination.SCAN_QR.route,
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f)
        ) {
//             Composable for the "Create QR" screen with transition animations
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
//             Composable for the "Scan QR" screen with transition animations
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
//             Composable for the "Decode Image" screen with transition animations
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

//         Bottom navigation bar with navigation items
        NavigationBar(
            navigationBarItems = navigationBarItems,
            selectedIndex = selectedIndex,
            onTabPressedEvent = { newIndex, route ->
                changeTab(
                    route,
                    navController
                ) {
                    previousIndex = selectedIndex
                    selectedIndex = newIndex
                }
            }
        )
    }
}