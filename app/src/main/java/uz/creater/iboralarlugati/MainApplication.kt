package uz.creater.iboralarlugati

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import uz.creater.iboralarlugati.utils.Settings

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (getSharedPreferences(
                Settings.PREF_NAME,
                Context.MODE_PRIVATE
            ).getBoolean(Settings.NIGHT_MODE_KEY, false)
        ) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        if (getSharedPreferences(
                Settings.PREF_LAN_NAME,
                Context.MODE_PRIVATE
            ).getBoolean(Settings.LAN_KEY, false)
        ) {
            Settings.isKiril = true
        }
    }
}