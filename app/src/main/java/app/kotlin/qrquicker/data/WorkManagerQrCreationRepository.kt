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

class WorkManagerQrCreationRepository(context: Context) : QrCreationRepository {
    private val workManager: WorkManager = WorkManager.getInstance(context)

    override fun createQrCode(text: String) {
        val data: Data = Data.Builder()
            .putString(KEY_TEXT_INPUT_VALUE, text)
            .build()

        val generateQrWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<GenerateQrWorker>()
                .setInputData(inputData = data)
                .build()

        val saveQrWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<SaveQrWorker>()
            .build()

        val continuation: WorkContinuation = workManager
            .beginWith(generateQrWorkRequest)
            .then(saveQrWorkRequest)

        continuation.enqueue()
    }
}