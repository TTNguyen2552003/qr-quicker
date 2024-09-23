package app.kotlin.qrquicker.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import app.kotlin.qrquicker.R
import app.kotlin.qrquicker.ui.styles.Elevation
import app.kotlin.qrquicker.ui.styles.bodyLarge
import app.kotlin.qrquicker.ui.styles.elevation
import app.kotlin.qrquicker.ui.styles.gap400
import app.kotlin.qrquicker.ui.styles.noScale
import app.kotlin.qrquicker.ui.styles.onSurfaceColor
import app.kotlin.qrquicker.ui.styles.onSurfaceVariantColor
import app.kotlin.qrquicker.ui.styles.shapeMedium
import app.kotlin.qrquicker.ui.styles.surfaceColor

@Composable
fun TextField(
    @StringRes placeHolder: Int,
    value: String = "",
    isReadOnly: Boolean = false,
    onValueChange: () -> Unit = {},
    clearInput: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .elevation(
                shape = RoundedCornerShape(size = shapeMedium),
                elevation = Elevation.EXTRA_LOW
            )
            .drawBehind {
                drawRoundRect(
                    color = surfaceColor,
                    cornerRadius = CornerRadius(shapeMedium.toPx())
                )
            }
            .padding(all = gap400),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            modifier = Modifier
                .weight(weight = 1f)
                .height(height = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            item {
                BasicTextField(
                    value = value,
                    onValueChange = { onValueChange() },
                    textStyle = bodyLarge
                        .copy(color = onSurfaceColor)
                        .noScale(),
                    readOnly = isReadOnly,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(value = onSurfaceColor),
                    decorationBox = { innerTextField ->
                        innerTextField()
                        if (value.isEmpty()) {
                            Text(
                                text = stringResource(id = placeHolder),
                                style = bodyLarge.noScale(),
                                color = onSurfaceVariantColor
                            )
                        }
                    }
                )
            }
        }

        if (value.isNotEmpty() && !isReadOnly) {
            Image(
                painter = painterResource(id = R.drawable.clear_icon),
                contentDescription = "clear text input icon",
                modifier = Modifier
                    .size(size = 20.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                tryAwaitRelease()
                                clearInput()
                            }
                        )
                    }
            )
        }
    }
}