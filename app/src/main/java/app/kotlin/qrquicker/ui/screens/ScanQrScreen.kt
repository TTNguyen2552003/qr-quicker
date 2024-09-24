package app.kotlin.qrquicker.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.kotlin.qrquicker.R
import app.kotlin.qrquicker.ui.components.Button
import app.kotlin.qrquicker.ui.components.CameraPreviewView
import app.kotlin.qrquicker.ui.components.OptionMenu
import app.kotlin.qrquicker.ui.components.TextField
import app.kotlin.qrquicker.ui.styles.bodySmall
import app.kotlin.qrquicker.ui.styles.gap400
import app.kotlin.qrquicker.ui.styles.gap600
import app.kotlin.qrquicker.ui.styles.gap700
import app.kotlin.qrquicker.ui.styles.noScale
import app.kotlin.qrquicker.ui.styles.onSurfaceColor
import app.kotlin.qrquicker.ui.styles.surfaceColor

private enum class ScanningAreaState {
    NO_CAMERA_PERMISSION,
    INACTIVE,
    ACTIVE
}

@Composable
fun ScanQrScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color = surfaceColor)
            }
            .padding(all = gap600),
        verticalArrangement = Arrangement.spacedBy(space = gap700),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context: Context = LocalContext.current

        val isCameraPermissionAllow: Boolean =
             context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        var scanningAreaState: ScanningAreaState by remember {
            mutableStateOf(
                value = if (isCameraPermissionAllow) {
                    ScanningAreaState.INACTIVE
                } else {
                    ScanningAreaState.NO_CAMERA_PERMISSION
                }
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(space = gap400),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (scanningAreaState) {
                ScanningAreaState.NO_CAMERA_PERMISSION -> {
                    Box(
                        modifier = Modifier.size(size = 200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.place_holder_outline),
                            contentDescription = "place holder outline",
                            modifier = Modifier.size(size = 160.dp),
                            contentScale = ContentScale.FillBounds,
                            colorFilter = ColorFilter.tint(color = onSurfaceColor)
                        )

                        Text(
                            text = stringResource(id = R.string.scan_qr_screen_scanning_area_place_holder_text_without_permission),
                            style = bodySmall.noScale(),
                            textAlign = TextAlign.Center,
                            color = onSurfaceColor
                        )
                    }

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    Button(
                        label = R.string.scan_qr_screen_request_permission_button_label,
                        onPressEvent = { context.startActivity(intent) }
                    )
                }

                ScanningAreaState.INACTIVE -> {
                    Image(
                        painter = painterResource(R.drawable.scanning_view_place_holder),
                        contentDescription = "camera view place holder",
                        modifier = Modifier.size(size = 200.dp),
                        colorFilter = ColorFilter.tint(color = onSurfaceColor)
                    )

                    Button(
                        label = R.string.scan_qr_screen_start_scanning_button_label,
                        onPressEvent = { scanningAreaState = ScanningAreaState.ACTIVE }
                    )
                }

                ScanningAreaState.ACTIVE -> {
                    CameraPreviewView()

                    Button(
                        label = R.string.scan_qr_screen_stop_scanning_button_label,
                        onPressEvent = { scanningAreaState = ScanningAreaState.INACTIVE }
                    )
                }
            }
        }

        TextField(placeHolder = R.string.scan_qr_screen_text_field_place_holder)

        OptionMenu(
            option1Description = R.string.scan_qr_screen_option_1_description,
            option2Description = R.string.scan_qr_screen_option_2_description,
        )
    }
}
