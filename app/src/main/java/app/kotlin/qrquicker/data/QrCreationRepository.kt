package app.kotlin.qrquicker.data

interface QrCreationRepository {
    fun createQrCode(text: String)
}