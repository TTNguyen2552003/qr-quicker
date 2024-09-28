package app.kotlin.qrquicker.workers

import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.kotlin.qrquicker.KEY_QR_CODE_OUTPUT_VALUE
import app.kotlin.qrquicker.SAVE_QR_FAILED_NOTIFICATION_BODY
import app.kotlin.qrquicker.SAVE_QR_FAILED_NOTIFICATION_ID
import app.kotlin.qrquicker.SAVE_QR_FAILED_NOTIFICATION_TITLE
import app.kotlin.qrquicker.SAVE_QR_SUCCESSFUL_NOTIFICATION_BODY
import app.kotlin.qrquicker.SAVE_QR_SUCCESSFUL_NOTIFICATION_ID
import app.kotlin.qrquicker.SAVE_QR_SUCCESSFUL_NOTIFICATION_TITLE
import app.kotlin.qrquicker.helpers.makeNotification
import app.kotlin.qrquicker.helpers.saveImageToMediaStore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class SaveQrWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext = appContext,
    params = workerParameters
) {

    private val titlePrefix = "QR Quicker"
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd 'at' HH:mm:ss z")
    private val now: ZonedDateTime = ZonedDateTime.now()

    override suspend fun doWork(): Result {
        return withContext(IO) {
            val contentResolver: ContentResolver = applicationContext.contentResolver
            return@withContext try {
                val qrCodeUri = inputData.getString(KEY_QR_CODE_OUTPUT_VALUE)
                val qrCodeBitmap = BitmapFactory.decodeStream(
                    contentResolver.openInputStream(Uri.parse(qrCodeUri))
                )
                val imageUri = saveImageToMediaStore(
                    contentResolver = contentResolver,
                    bitmap = qrCodeBitmap,
                    title = titlePrefix + formatter.format(now)
                )

                if (imageUri != null) {
                    makeNotification(
                        context = applicationContext,
                        title = SAVE_QR_SUCCESSFUL_NOTIFICATION_TITLE,
                        body = SAVE_QR_SUCCESSFUL_NOTIFICATION_BODY,
                        id = SAVE_QR_SUCCESSFUL_NOTIFICATION_ID
                    )
                    Result.success()
                } else {
                    makeNotification(
                        context = applicationContext,
                        title = SAVE_QR_FAILED_NOTIFICATION_TITLE,
                        body = SAVE_QR_FAILED_NOTIFICATION_BODY,
                        id = SAVE_QR_FAILED_NOTIFICATION_ID
                    )
                    Result.failure()
                }
            } catch (throwable: Throwable) {
                makeNotification(
                    context = applicationContext,
                    title = SAVE_QR_FAILED_NOTIFICATION_TITLE,
                    body = SAVE_QR_FAILED_NOTIFICATION_BODY,
                    id = SAVE_QR_FAILED_NOTIFICATION_ID
                )
                Result.failure()
            }
        }
    }
}