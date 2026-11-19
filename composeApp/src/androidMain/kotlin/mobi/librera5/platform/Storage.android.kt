package mobi.librera5.platform

import android.content.Context
import android.content.SharedPreferences

actual class Storage(private val prefs: SharedPreferences) {
    actual fun getString(key: String, defaultValue: String): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    actual fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    actual fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    actual fun getInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    actual fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }
}

private var appContext: Context? = null

fun initAndroidStorage(context: Context) {
    appContext = context.applicationContext
}

actual fun getAppStorage(): Storage {
    val ctx = appContext ?: throw IllegalStateException("Storage not initialized on Android")
    val prefs = ctx.getSharedPreferences("librera_prefs", Context.MODE_PRIVATE)
    return Storage(prefs)
}
