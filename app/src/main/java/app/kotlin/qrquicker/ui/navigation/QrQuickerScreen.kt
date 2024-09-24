package app.kotlin.qrquicker.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
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
import app.kotlin.qrquicker.R
import app.kotlin.qrquicker.ui.components.NavigationBar
import app.kotlin.qrquicker.ui.components.NavigationBarItem
import app.kotlin.qrquicker.ui.screens.CreateQrScreen
import app.kotlin.qrquicker.ui.screens.DecodeImageScreen
import app.kotlin.qrquicker.ui.screens.ScanQrScreen
import app.kotlin.qrquicker.ui.styles.pushLeftTransition
import app.kotlin.qrquicker.ui.styles.pushRightTransition

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

@Composable
fun QrQuickerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        var selectedIndex: Int by remember {
            mutableIntStateOf(value = Destination.SCAN_QR.tabIndex)
        }
        var previousIndex: Int by remember {
            mutableIntStateOf(value = selectedIndex)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f)
        ) {
            AnimatedContent(
                targetState = selectedIndex,
                label = "navigation section",
                transitionSpec = {
                    if (previousIndex < selectedIndex)
                        pushRightTransition
                    else
                        pushLeftTransition
                }
            ) { targetState ->
                when (targetState) {
                    Destination.CREATE_QR.tabIndex -> CreateQrScreen()
                    Destination.SCAN_QR.tabIndex -> ScanQrScreen()
                    Destination.DECODE_IMAGE.tabIndex -> DecodeImageScreen()
                }
            }
        }

        val changeTab: (Int) -> Unit = { newIndex: Int ->
            previousIndex = selectedIndex
            selectedIndex = newIndex
        }

        NavigationBar(
            navigationBarItems = navigationBarItems,
            selectedIndex = selectedIndex,
            onTabPressedEvent = changeTab
        )
    }
}