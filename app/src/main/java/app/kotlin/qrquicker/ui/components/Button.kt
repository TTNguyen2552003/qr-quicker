package app.kotlin.qrquicker.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import app.kotlin.qrquicker.ui.styles.gap300
import app.kotlin.qrquicker.ui.styles.gap600
import app.kotlin.qrquicker.ui.styles.labelLarge
import app.kotlin.qrquicker.ui.styles.noScale
import app.kotlin.qrquicker.ui.styles.onPrimaryColor
import app.kotlin.qrquicker.ui.styles.primaryColor
import app.kotlin.qrquicker.ui.styles.shapeMedium

@Composable
fun Button(
    @StringRes label: Int,
    onPressEvent: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .drawBehind {
                drawRoundRect(
                    color = primaryColor,
                    cornerRadius = CornerRadius(shapeMedium.toPx())
                )
            }
            .padding(
                horizontal = gap600,
                vertical = gap300
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        tryAwaitRelease()
                        onPressEvent()
                    }
                )
            }
    ) {
        Text(
            text = stringResource(id = label),
            style = labelLarge.noScale(),
            color = onPrimaryColor
        )
    }
}
