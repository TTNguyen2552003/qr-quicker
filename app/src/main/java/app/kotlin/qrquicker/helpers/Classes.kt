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

class QrCodeAnalyzer(
    private val onQrCodeDetected: (String) -> Unit = {},
    private val onScanFailed: () -> Unit = {}
) : ImageAnalysis.Analyzer {
    private val scannerOptions: BarcodeScannerOptions = BarcodeScannerOptions
        .Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    private val scanner: BarcodeScanner = BarcodeScanning.getClient(scannerOptions)

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