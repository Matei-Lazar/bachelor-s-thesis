package ths.kariru

import android.app.Application
import timber.log.Timber

class KariruApplication : Application() {
    override fun onCreate() {
        super.onCreate()
         Timber.plant(Timber.DebugTree())
    }
}