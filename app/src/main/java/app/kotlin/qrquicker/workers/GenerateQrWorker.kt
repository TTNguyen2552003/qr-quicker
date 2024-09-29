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
//         Show a notification to inform the user that the QR code generation process has started
        makeNotification(
            context = applicationContext,
            title = SAVING_QR_NOTIFICATION_TITLE,
            body = SAVING_QR_NOTIFICATION_BODY,
            id = SAVING_QR_NOTIFICATION_ID
        )

//         Perform the work on the IO dispatcher to avoid blocking the main thread
        return withContext(IO) {
            return@withContext try {
//                 Retrieve the input text from the worker's input data
                val textInput: String = inputData.getString(KEY_TEXT_INPUT_VALUE) ?: ""

//                 Generate the QR code bitmap from the input text
                val qrCodeResult: Bitmap = generateQRCode(text = textInput)

//                 Write the bitmap to a file and get its URI
                val qrCodeResultUri: Uri = writeBitmapToFile(applicationContext, qrCodeResult)

//                 Prepare the output data with the QR code file URI
                val outputData: Data =
                    workDataOf(KEY_QR_CODE_OUTPUT_VALUE to qrCodeResultUri.toString())

//                 Return a successful result with the output data for saving in the next chain
//                of the work continuation
                Result.success(outputData)
            } catch (throwable: Throwable) {
//                 If an error occurs, show a failure notification
                makeNotification(
                    context = applicationContext,
                    title = SAVE_QR_FAILED_NOTIFICATION_TITLE,
                    body = SAVE_QR_FAILED_NOTIFICATION_BODY,
                    id = SAVE_QR_FAILED_NOTIFICATION_ID
                )

//                 Return a failure result
                Result.failure()
            }
        }
    }
}