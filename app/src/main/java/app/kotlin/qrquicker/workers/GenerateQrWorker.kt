package app.kotlin.qrquicker.workers

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import app.kotlin.qrquicker.KEY_QR_CODE_OUTPUT_VALUE
import app.kotlin.qrquicker.KEY_TEXT_INPUT_VALUE
import app.kotlin.qrquicker.SAVE_QR_FAILED_NOTIFICATION_BODY
import app.kotlin.qrquicker.SAVE_QR_FAILED_NOTIFICATION_ID
import app.kotlin.qrquicker.SAVE_QR_FAILED_NOTIFICATION_TITLE
import app.kotlin.qrquicker.SAVING_QR_NOTIFICATION_BODY
import app.kotlin.qrquicker.SAVING_QR_NOTIFICATION_ID
import app.kotlin.qrquicker.SAVING_QR_NOTIFICATION_TITLE
import app.kotlin.qrquicker.helpers.generateQRCode
import app.kotlin.qrquicker.helpers.makeNotification
import app.kotlin.qrquicker.helpers.writeBitmapToFile
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class GenerateQrWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext = appContext,
    params = workerParameters
) {
    override suspend fun doWork(): Result {
        makeNotification(
            context = applicationContext,
            title = SAVING_QR_NOTIFICATION_TITLE,
            body = SAVING_QR_NOTIFICATION_BODY,
            id = SAVING_QR_NOTIFICATION_ID
        )
        return withContext(IO) {
            return@withContext try {
                val textInput: String = inputData.getString(KEY_TEXT_INPUT_VALUE) ?: ""
                val qrCodeResult: Bitmap = generateQRCode(text = textInput)
                val qrCodeResultUri: Uri = writeBitmapToFile(applicationContext, qrCodeResult)
//                After generating the QR code in the this worker, pass it to the save QR worker by using WorkData
                val outputData: Data =
                    workDataOf(KEY_QR_CODE_OUTPUT_VALUE to qrCodeResultUri.toString())
                Result.success(outputData)
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