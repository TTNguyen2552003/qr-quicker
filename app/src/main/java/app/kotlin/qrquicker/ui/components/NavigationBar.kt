package app.kotlin.qrquicker.ui.components

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.kotlin.qrquicker.ui.styles.Elevation
import app.kotlin.qrquicker.ui.styles.TRANSITION_DURATION
import app.kotlin.qrquicker.ui.styles.elevation
import app.kotlin.qrquicker.ui.styles.gap300
import app.kotlin.qrquicker.ui.styles.gap600
import app.kotlin.qrquicker.ui.styles.primaryColor
import app.kotlin.qrquicker.ui.styles.shapeLarge
import app.kotlin.qrquicker.ui.styles.shapeMedium
import app.kotlin.qrquicker.ui.styles.surfaceColor

@Composable
fun NavigationBar(
    navigationBarItems: List<NavigationBarItem>,
    selectedIndex: Int,
    onTabPressedEvent: (Int, String) -> Unit
) {
    // Tab indicator size in dp (consider externalizing to a dimension resource)
    val tabIndicatorSize = 60

    // Padding and margins
    val totalHorizontalPadding = gap600.value * 4

    // Screen width and number of navigation items
    val navWidth: Int = LocalConfiguration.current.screenWidthDp
    val numberOfItems: Int = navigationBarItems.size

    // Calculate the distance between navigation items
    val totalUsedWidth = numberOfItems * tabIndicatorSize + totalHorizontalPadding
    val itemDistance: Float = (navWidth - totalUsedWidth) / (numberOfItems - 1)

    // Animate tab indicator offset along the x-axis
    val tabIndicatorOffsetX: Dp by animateDpAsState(
        targetValue = gap600 + (selectedIndex * (itemDistance + tabIndicatorSize)).dp,
        animationSpec = tween(
            durationMillis = TRANSITION_DURATION,
            easing = EaseOut
        ),
        label = "tab indicator offset x-axis"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(color = surfaceColor)
            }
            .padding(
                start = gap600,
                end = gap600,
                bottom = gap600
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .elevation(
                    shape = RoundedCornerShape(size = shapeLarge),
                    elevation = Elevation.EXTRA_LOW
                )
                .drawBehind {
                    drawRoundRect(
                        color = surfaceColor,
                        cornerRadius = CornerRadius(shapeLarge.toPx())
                    )

                    drawRoundRect(
                        color = primaryColor,
                        cornerRadius = CornerRadius(shapeMedium.toPx()),
                        size = Size(
                            width = tabIndicatorSize.dp.toPx(),
                            height = tabIndicatorSize.dp.toPx()
                        ),
                        topLeft = Offset(
                            x = tabIndicatorOffsetX.toPx(),
                            y = gap300.toPx()
                        )
                    )
                }
                .padding(
                    horizontal = gap600,
                    vertical = gap300
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navigationBarItems.forEachIndexed { index, navigationBarItem ->
                NavigationBarItem(
                    isSelected = (index == selectedIndex),
                    icon = navigationBarItem.icon,
                    iconAltText = navigationBarItem.iconAltText,
                    label = navigationBarItem.label,
                    onPressEvent = {
                        onTabPressedEvent(index, navigationBarItem.route)
                    }
                )
            }
        }
    }
}