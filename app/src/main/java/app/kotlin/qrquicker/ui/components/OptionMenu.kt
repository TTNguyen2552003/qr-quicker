package app.kotlin.qrquicker.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import app.kotlin.qrquicker.R
import app.kotlin.qrquicker.ui.styles.Elevation
import app.kotlin.qrquicker.ui.styles.elevation
import app.kotlin.qrquicker.ui.styles.gap400
import app.kotlin.qrquicker.ui.styles.gap600
import app.kotlin.qrquicker.ui.styles.noScale
import app.kotlin.qrquicker.ui.styles.onSurfaceColor
import app.kotlin.qrquicker.ui.styles.shapeLarge
import app.kotlin.qrquicker.ui.styles.surfaceColor
import app.kotlin.qrquicker.ui.styles.title

@Composable
fun OptionMenu(
    @StringRes option1Description: Int,
    option1State: Boolean = false,
    onOption1StateChange: () -> Unit = {},
    @StringRes option2Description: Int,
    option2State: Boolean = false,
    onOption2StateChange: () -> Unit = {}
) {
    Column(
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
            }
            .padding(
                start = gap400,
                top = gap400,
                end = gap400,
                bottom = gap600
            ),
        verticalArrangement = Arrangement.spacedBy(space = gap600),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.option_menu_title),
            style = title.noScale(),
            color = onSurfaceColor
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(space = gap400),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Option(
                optionState = option1State,
                description = option1Description,
                onStateChange = onOption1StateChange
            )

            Option(
                optionState = option2State,
                description = option2Description,
                onStateChange = onOption2StateChange
            )
        }
    }
}