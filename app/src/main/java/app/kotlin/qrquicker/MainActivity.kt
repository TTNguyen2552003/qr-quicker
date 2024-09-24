package app.kotlin.qrquicker

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import app.kotlin.qrquicker.ui.navigation.QrQuickerScreen
import app.kotlin.qrquicker.ui.styles.onSurfaceColor
import app.kotlin.qrquicker.ui.styles.surfaceColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = surfaceColor.toArgb(),
                darkScrim = onSurfaceColor.toArgb()
            )
        )
        setContent {
//            Lock orientation
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            QrQuickerScreen()
        }
    }
}