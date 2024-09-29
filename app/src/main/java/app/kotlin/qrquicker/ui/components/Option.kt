package app.kotlin.qrquicker.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.res.stringResource
import app.kotlin.qrquicker.ui.styles.Elevation
import app.kotlin.qrquicker.ui.styles.bodySmall
import app.kotlin.qrquicker.ui.styles.elevation
import app.kotlin.qrquicker.ui.styles.gap300
import app.kotlin.qrquicker.ui.styles.noScale
import app.kotlin.qrquicker.ui.styles.onSurfaceColor
import app.kotlin.qrquicker.ui.styles.shapeMedium
import app.kotlin.qrquicker.ui.styles.surfaceColor

/**
 * A composable function that displays a toggle option with a description and manages its state.
 *
 * @param optionState The current state of the option (true for active, false for inactive). Defaults to false.
 * @param description The string resource ID for the option's description text.
 * @param onStateChange A callback function that is triggered when the state changes (i.e., when the toggle is pressed).
 */
@Composable
fun Option(
    optionState: Boolean = false,
    @StringRes description: Int,
    onStateChange: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .elevation(
                shape = RoundedCornerShape(size = shapeMedium),
                elevation = Elevation.LOW
            )
            .drawBehind {
                drawRoundRect(
                    color = surfaceColor,
                    cornerRadius = CornerRadius(shapeMedium.toPx())
                )
            }
            .padding(all = gap300),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
//         Displays the description of the option.
        Text(
            text = stringResource(id = description),
            style = bodySmall.noScale(),
            color = onSurfaceColor
        )

//         Displays a toggle button that represents the state of the option.
        Toggle(
            state = optionState,
            onStateChange = onStateChange
        )
    }
}
