package mobi.librera5.platform

import java.util.prefs.Preferences

actual class Storage(private val prefs: Preferences) {
    actual fun getString(key: String, defaultValue: String): String {
        return prefs.get(key, defaultValue)
    }

    actual fun putString(key: String, value: String) {
        prefs.put(key, value)
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    actual fun putBoolean(key: String, value: Boolean) {
        prefs.putBoolean(key, value)
    }

    actual fun getInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    actual fun putInt(key: String, value: Int) {
        prefs.putInt(key, value)
    }
}

private val desktopStorageInstance by lazy {
    Storage(Preferences.userNodeForPackage(Storage::class.java))
}

actual fun getAppStorage(): Storage = desktopStorageInstance
