package app.kotlin.qrquicker.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.kotlin.qrquicker.TRANSITION_DURATION
import app.kotlin.qrquicker.ui.styles.gap100
import app.kotlin.qrquicker.ui.styles.labelSmall
import app.kotlin.qrquicker.ui.styles.noScale
import app.kotlin.qrquicker.ui.styles.onPrimaryColor
import app.kotlin.qrquicker.ui.styles.onSurfaceColor

/**
 * A composable function to represent the tab in the navigation bar
 *
 * @param icon associated with navigation tab in the navigation bar
 * @param iconAltText is accessibility description for the icon
 * @param label is the label of tab
 * @param route is the destination that the tab navigate to
 */
data class NavigationBarItem(
    @DrawableRes val icon: Int,
    val iconAltText: String = "",
    @StringRes val label: Int,
    val route: String = ""
)

@Composable
fun NavigationBarItem(
    isSelected: Boolean = false,
    @DrawableRes icon: Int,
    iconAltText: String = "",
    @StringRes label: Int,
    onPressEvent: () -> Unit = {}
) {
    val navigationBarItemColor: Color by animateColorAsState(
        targetValue = if (isSelected) {
            onPrimaryColor
        } else {
            onSurfaceColor
        },
        animationSpec = tween(
            durationMillis = TRANSITION_DURATION,
            easing = EaseOut
        ),
        label = "navigation bar icon and text color"
    )

    Box(
        modifier = Modifier
            .size(size = 60.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPressEvent()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = gap100),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = iconAltText,
                modifier = Modifier.size(size = 24.dp),
                colorFilter = ColorFilter.tint(color = navigationBarItemColor)
            )

            Text(
                text = stringResource(id = label),
                style = labelSmall.noScale(),
                color = navigationBarItemColor
            )
        }
    }
}