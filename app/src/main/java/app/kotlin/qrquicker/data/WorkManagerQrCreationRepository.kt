package app.kotlin.qrquicker.data

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import app.kotlin.qrquicker.KEY_TEXT_INPUT_VALUE
import app.kotlin.qrquicker.workers.GenerateQrWorker
import app.kotlin.qrquicker.workers.SaveQrWorker

/**
 * A repository that handles creating QR codes using WorkManager.
 * It enqueues background tasks to generate and save QR codes.
 *
 * @param context The context used for create WorkManager instance
 */
class WorkManagerQrCreationRepository(context: Context) : QrCreationRepository {
//    Initialize WorkManager instance for managing work requests
    private val workManager: WorkManager = WorkManager.getInstance(context)

    /**
     * Creates a QR code by enqueuing work requests to generate and save it.
     *
     * @param text The text input to encode into a QR code.
     */
    override fun createQrCode(text: String) {
//        Step 1: Create input data containing the text to encode in the QR code
        val data: Data = Data.Builder()
            .putString(KEY_TEXT_INPUT_VALUE, text) // Pass text input as data
            .build()

//        Step 2: Define a OneTimeWorkRequest to generate the QR code in the background
        val generateQrWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<GenerateQrWorker>()
                .setInputData(inputData = data) // Pass the input data to the worker
                .build()

//        Step 3: Define another OneTimeWorkRequest to save the generated QR code
        val saveQrWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<SaveQrWorker>()
                .build()

//        Step 4: Chain the work requests so that the QR code is generated first,
//        and once it's done, the result is saved.
        val continuation: WorkContinuation = workManager
            .beginWith(generateQrWorkRequest) // Start with QR generation
            .then(saveQrWorkRequest)          // Then save the generated QR code

//        Step 5: Enqueue the chained work requests to be executed by WorkManager
        continuation.enqueue()
    }
}