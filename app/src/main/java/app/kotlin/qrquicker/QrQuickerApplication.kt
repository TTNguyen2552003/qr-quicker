package app.kotlin.qrquicker

import android.app.Application
import app.kotlin.qrquicker.data.AppContainer
import app.kotlin.qrquicker.data.DefaultAppContainer

class QrQuickerApplication : Application() {
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(context = applicationContext)
    }
}