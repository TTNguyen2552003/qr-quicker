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

@Composable
fun CameraPreviewView(
    onQrCodeDetected: (String) -> Unit = { _ -> },
    onScanFailed: () -> Unit = {},
    torchEnabled: Boolean = false
) {
    val context: Context = LocalContext.current
    val currentLifecycleOwner = LocalLifecycleOwner.current
    val cameraController: LifecycleCameraController = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(currentLifecycleOwner)
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                QrCodeAnalyzer(
                    onQrCodeDetected = onQrCodeDetected,
                    onScanFailed = onScanFailed
                )
            )
        }
    }

    val previewView: PreviewView = remember { PreviewView(context) }

    AndroidView(
        factory = { previewView },
        modifier = Modifier
            .size(size = 200.dp)
            .clip(shape = RectangleShape),
        update = { view ->
            view.controller = cameraController
            cameraController.enableTorch(torchEnabled)
        }
    )
}