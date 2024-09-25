package app.kotlin.qrquicker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.kotlin.qrquicker.ui.styles.Elevation
import app.kotlin.qrquicker.ui.styles.elevation
import app.kotlin.qrquicker.ui.styles.primaryColor
import app.kotlin.qrquicker.ui.styles.shapeLarge
import app.kotlin.qrquicker.ui.styles.stateLayerInactiveColor
import app.kotlin.qrquicker.ui.styles.surfaceColor

@Composable
fun Toggle(
    state: Boolean = false,
    onStateChange: () -> Unit
) {
    val toggleThumbOffsetX: Dp by animateDpAsState(
        targetValue = if (state) {
            28.dp
        } else {
            4.dp
        },
        animationSpec = tween(
            durationMillis = 300,
            easing = EaseOut
        ),
        label = "toggle thumb offset x-axis"
    )
    val toggleBackgroundColor: Color by animateColorAsState(
        targetValue = if (state) {
            primaryColor
        } else {
            stateLayerInactiveColor
        },
        animationSpec = tween(
            durationMillis = 300,
            easing = EaseOut
        ),
        label = "toggle background color"
    )
    Box(
        modifier = Modifier
            .width(width = 56.dp)
            .height(height = 32.dp)
            .drawBehind {
                drawRoundRect(
                    color = toggleBackgroundColor,
                    cornerRadius = CornerRadius(shapeLarge.toPx())
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onStateChange()
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = toggleThumbOffsetX)
                .size(size = 24.dp)
                .elevation(
                    shape = CircleShape,
                    elevation = Elevation.LOW
                )
                .drawBehind {
                    drawCircle(color = surfaceColor)
                }
        )
    }
}