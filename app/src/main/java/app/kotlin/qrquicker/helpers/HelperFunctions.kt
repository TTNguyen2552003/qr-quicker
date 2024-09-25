package app.kotlin.qrquicker.helpers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.webkit.URLUtil
import android.widget.Toast
import app.kotlin.qrquicker.R
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage


fun gotoAppSetting(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }

    context.startActivity(intent)
}

fun copyToClipBoard(context: Context, textCopy: String) {
    val clipBoardManager: ClipboardManager = context
        .getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

    val clip: ClipData = ClipData.newPlainText(
        "qr code with auto copy",
        textCopy
    )

    clipBoardManager.setPrimaryClip(clip)

    Toast.makeText(context, context.getString(R.string.toast_message_auto_copy_to_clipboard_option), Toast.LENGTH_LONG)
        .show()
}

fun openWeblink(context: Context, weblink: String) {
    if (URLUtil.isHttpsUrl(weblink)) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(weblink))
        context.startActivity(browserIntent)
    }
}

fun analyzeImage(
    context: Context,
    imageUri: Uri?,
    onQrCodeDetected: (String) -> Unit = { _ -> },
    onDetectFailed: () -> Unit = {}
) {
    val currentUri: Uri? = imageUri
    val inputImage: InputImage? = currentUri?.let { InputImage.fromFilePath(context, it) }
    val barcodeScanningOptions: BarcodeScannerOptions = BarcodeScannerOptions
        .Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    val scanner: BarcodeScanner = BarcodeScanning.getClient(barcodeScanningOptions)

    if (inputImage != null) {
        try {
            scanner.process(inputImage)
                .addOnSuccessListener { qrCode ->
                    if (qrCode.isNotEmpty()) {
                        onQrCodeDetected(qrCode[0].rawValue ?: "")
                    }
                }
                .addOnFailureListener {
                    onDetectFailed()
                }
        } catch (throwable: Throwable) {
            onDetectFailed()
        }
    }
}