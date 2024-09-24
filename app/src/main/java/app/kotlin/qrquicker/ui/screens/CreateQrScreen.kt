package app.kotlin.qrquicker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.kotlin.qrquicker.R
import app.kotlin.qrquicker.ui.components.Button
import app.kotlin.qrquicker.ui.components.TextField
import app.kotlin.qrquicker.ui.styles.bodyLarge
import app.kotlin.qrquicker.ui.styles.gap300
import app.kotlin.qrquicker.ui.styles.gap600
import app.kotlin.qrquicker.ui.styles.noScale
import app.kotlin.qrquicker.ui.styles.onSurfaceColor
import app.kotlin.qrquicker.ui.styles.surfaceColor
import kotlin.random.Random

@Preview
@Composable
fun CreateQrScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color = surfaceColor)
            }
            .padding(all = gap600)
    ) {
        TextField(placeHolder = R.string.create_qr_screen_text_field_place_holder)

        val chance: Boolean = Random.nextBoolean()

        if (chance) {
            Box(
                modifier = Modifier
                    .size(size = 256.dp)
                    .align(alignment = Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.place_holder_outline),
                    contentDescription = "",
                    modifier = Modifier.size(size = 256.dp),
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(color = onSurfaceColor)
                )

                Text(
                    text = stringResource(id = R.string.create_qr_screen_result_place_holder_text),
                    style = bodyLarge.noScale(),
                    color = onSurfaceColor
                )
            }
        } else {
            Column(
                modifier = Modifier.align(alignment = Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(space = gap300),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sample_qr_code_result),
                    contentDescription = "",
                    modifier = Modifier.size(size = 256.dp),
                    contentScale = ContentScale.FillBounds
                )

                Button(label = R.string.create_qr_screen_save_button_label)
            }
        }
    }
}