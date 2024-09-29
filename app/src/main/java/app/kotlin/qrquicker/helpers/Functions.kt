package app.kotlin.qrquicker.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.URLUtil
import android.widget.Toast
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.kotlin.qrquicker.CHANNEL_DESCRIPTION
import app.kotlin.qrquicker.CHANNEL_ID
import app.kotlin.qrquicker.CHANNEL_NAME
import app.kotlin.qrquicker.OUTPUT_PATH
import app.kotlin.qrquicker.QR_CODE_RESULT_IMAGE_SIZE
import app.kotlin.qrquicker.R
import app.kotlin.qrquicker.SAVE_QR_FAILED_NOTIFICATION_BODY
import app.kotlin.qrquicker.SAVE_QR_FAILED_NOTIFICATION_ID
import app.kotlin.qrquicker.SAVE_QR_FAILED_NOTIFICATION_TITLE
import app.kotlin.qrquicker.SAVE_QR_SUCCESSFUL_NOTIFICATION_ID
import app.kotlin.qrquicker.ui.styles.onSurfaceColor
import app.kotlin.qrquicker.ui.styles.primaryColor
import app.kotlin.qrquicker.ui.styles.surfaceColor
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

/**
 * Opens the application settings page for the current app.
 *
 * @param context The context used to start the activity.
 */
fun gotoAppSetting(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

/**
 * Copies the given text to the clipboard and shows a toast message.
 *
 * @param context The context used to access the clipboard service and show the toast.
 * @param textCopy The text to be copied to the clipboard.
 */
fun copyToClipBoard(context: Context, textCopy: String) {
    val clipBoardManager: ClipboardManager = context
        .getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

    val clip: ClipData = ClipData.newPlainText(
        "qr code with auto copy",
        textCopy
    )

    clipBoardManager.setPrimaryClip(clip)

    Toast.makeText(
        context,
        context.getString(R.string.toast_message_auto_copy_to_clipboard_option),
        Toast.LENGTH_LONG
    ).show()
}

/**
 * Opens a web link in the default browser if it's a valid HTTPS URL.
 *
 * @param context The context used to start the activity.
 * @param weblink The URL to be opened.
 */
fun openWeblink(context: Context, weblink: String) {
    if (URLUtil.isHttpsUrl(weblink)) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(weblink))
        context.startActivity(browserIntent)
    }
}

/**
 * Analyzes an image for QR codes using ML Kit's Barcode Scanning API.
 *
 * @param context The context used to create the InputImage.
 * @param imageUri The URI of the image to be analyzed.
 * @param onQrCodeDetected Callback function when a QR code is detected.
 * @param onDetectFailed Callback function when detection fails.
 */
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

/**
 * Generates a QR code bitmap from the given text.
 *
 * @param text The text to be encoded in the QR code.
 * @return A Bitmap representing the generated QR code.
 */
fun generateQRCode(text: String): Bitmap {
    val width = QR_CODE_RESULT_IMAGE_SIZE
    val height = QR_CODE_RESULT_IMAGE_SIZE

    val bitMatrix: BitMatrix = MultiFormatWriter().encode(
        text,
        BarcodeFormat.QR_CODE,
        width,
        height
    )

    val bitmap: Bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.RGBA_F16
    )

    for (x: Int in 0 until width) {
        for (y: Int in 0 until height) {
            bitmap.setPixel(
                x,
                y,
                if (bitMatrix[x, y]) onSurfaceColor.toArgb() else surfaceColor.toArgb()
            )
        }
    }

    return bitmap
}

/**
 * Writes a bitmap to a file in the app's internal storage.
 *
 * @param applicationContext The context used to access the app's file system.
 * @param bitmap The bitmap to be written to the file.
 * @return The URI of the written file.
 * @throws FileNotFoundException If the file cannot be created or opened.
 *
 */
@Throws(FileNotFoundException::class)
fun writeBitmapToFile(applicationContext: Context, bitmap: Bitmap): Uri {
    val name: String = String.format("qr-code-output-%s.png", UUID.randomUUID().toString())
    val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)
    if (!outputDir.exists()) {
        outputDir.mkdirs() // should succeed
    }
    val outputFile = File(outputDir, name)
    var out: FileOutputStream? = null
    try {
        out = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, out)
    } finally {
        out?.let {
            try {
                it.close()
            } catch (e: IOException) {
                makeNotification(
                    context = applicationContext,
                    title = SAVE_QR_FAILED_NOTIFICATION_TITLE,
                    body = SAVE_QR_FAILED_NOTIFICATION_BODY,
                    id = SAVE_QR_FAILED_NOTIFICATION_ID
                )
            }
        }
    }
    return Uri.fromFile(outputFile)
}

/**
 * Saves an image to the device's MediaStore.
 *
 * @param contentResolver The ContentResolver used to interact with the MediaStore.
 * @param bitmap The bitmap to be saved.
 * @param title The title of the image (without extension).
 * @return The URI of the saved image, or null if saving failed.
 */
fun saveImageToMediaStore(
    contentResolver: ContentResolver,
    bitmap: Bitmap,
    title: String
): Uri? {
    val contentValues: ContentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$title.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
    }

//     Define where to save the image (external storage directory or public pictures)
    val imageUri: Uri? = contentResolver.insert(
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
        contentValues
    )

    imageUri?.let {
//         Open output stream to the URI and write the bitmap data
        contentResolver.openOutputStream(it).use { outputStream: OutputStream? ->
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
            }
        }
    }

    return imageUri
}

/**
 * Creates and shows a notification.
 *
 * @param context The context used to create and show the notification.
 * @param title The title of the notification.
 * @param body The body text of the notification.
 * @param id The unique identifier for the notification.
 */
fun makeNotification(
    context: Context,
    title: String,
    body: String,
    id: Int
) {
    val name: CharSequence = CHANNEL_NAME
    val description: String = CHANNEL_DESCRIPTION
    val channelId: String = CHANNEL_ID
    val important: Int = NotificationManager.IMPORTANCE_HIGH
    val largeIcon: Bitmap? = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.ic_launcher_foreground
        )
    val channel = NotificationChannel(channelId, name, important).also {
        it.description = description
    }

    val notificationManager: NotificationManager? =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

    notificationManager?.createNotificationChannel(channel)

    val notificationBuilder: NotificationCompat.Builder =
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(size = 0))
            .setAutoCancel(true)
            .setColor(primaryColor.toArgb())

//     Add a content intent to open the gallery for successful QR code saves
    if (id == SAVE_QR_SUCCESSFUL_NOTIFICATION_ID) {
        val intent = Intent(Intent.ACTION_VIEW, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val pendingIntent: PendingIntent = PendingIntent
            .getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        notificationBuilder.setContentIntent(pendingIntent)
    }

    NotificationManagerCompat.from(context).notify(id, notificationBuilder.build())
}