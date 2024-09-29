package app.kotlin.qrquicker.helpers

import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * A class that analyzes images to detect QR codes.
 *
 * @property onQrCodeDetected A callback function that is triggered when a QR code is detected.
 * The function receives the QR code's raw value as a String.
 * @property onScanFailed A callback function that is triggered when QR code detection fails.
 */
class QrCodeAnalyzer(
    private val onQrCodeDetected: (String) -> Unit = {},
    private val onScanFailed: () -> Unit = {}
) : ImageAnalysis.Analyzer {
//     Configures the scanner to detect only QR codes.
    private val scannerOptions: BarcodeScannerOptions = BarcodeScannerOptions
        .Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

//     Initializes the QR code scanner client with the specified options.
    private val scanner: BarcodeScanner = BarcodeScanning.getClient(scannerOptions)

    /**
     * Analyzes the given image frame and attempts to detect QR codes.
     *
     * @param imageProxy The image to analyze, wrapped in an ImageProxy.
     *
     * This method processes the image using Google's BarcodeScanner, which scans for QR codes.
     * If a QR code is successfully detected, the `onQrCodeDetected` callback is invoked.
     * If the scanning fails, the `onScanFailed` callback is triggered.
     * Once the process is complete, the `imageProxy` is closed.
     */
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage: Image? = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            scanner.process(image)
                .addOnSuccessListener { qrCode ->
                    if (qrCode.isNotEmpty()) {
                        onQrCodeDetected(qrCode[0].rawValue ?: "")
                    }
                }
                .addOnFailureListener {
                    onScanFailed()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}
