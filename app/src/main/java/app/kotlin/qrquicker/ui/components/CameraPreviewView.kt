package app.kotlin.qrquicker.ui.components

import android.content.Context
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import app.kotlin.qrquicker.helpers.QrCodeAnalyzer

/**
 * @param onQrCodeDetected is invoked when the QR code detected
 * It receives the QR code result as a String
 * @param onScanFailed is invoked when the the app cannot detected the QR
 * @param torchEnabled detect if the flashlight should be turned on or not
 */
@Composable
fun CameraPreviewView(
    onQrCodeDetected: (String) -> Unit = { _ -> },
    onScanFailed: () -> Unit = {},
    torchEnabled: Boolean = false
) {
//     Get the current Android context
    val context: Context = LocalContext.current

//     Get the current lifecycle owner (typically the activity or fragment)
    val currentLifecycleOwner = LocalLifecycleOwner.current

//     Create and configure the camera controller
    val cameraController: LifecycleCameraController = remember {
        LifecycleCameraController(context).apply {
//             Bind the controller to the lifecycle to manage camera resources
            bindToLifecycle(currentLifecycleOwner)

//             Set up the image analyzer for QR code detection
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                QrCodeAnalyzer(
                    onQrCodeDetected = onQrCodeDetected,
                    onScanFailed = onScanFailed
                )
            )
        }
    }

//     Create the PreviewView that will display the camera feed
    val previewView: PreviewView = remember { PreviewView(context) }

//     Use AndroidView to integrate the PreviewView into the Compose UI
    AndroidView(
        factory = { previewView },
        modifier = Modifier
            .size(size = 200.dp)
            .clip(shape = RectangleShape),
        update = { view ->
//             Set the controller for the view and update torch state
            view.controller = cameraController
            cameraController.enableTorch(torchEnabled)
        }
    )
}