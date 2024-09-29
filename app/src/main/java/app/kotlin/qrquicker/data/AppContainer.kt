package app.kotlin.qrquicker.data

import android.content.Context

interface AppContainer {
    val qrCreationRepository: QrCreationRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    override val qrCreationRepository: QrCreationRepository =
        WorkManagerQrCreationRepository(context = context)
}