package mobi.librera5.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

class DesktopPlatform : Platform {
    override val name: String = "Desktop JVM (${System.getProperty("os.name")}, Java ${System.getProperty("java.version")})"
    override val isDesktop: Boolean = true
    override val isAndroid: Boolean = false
}

actual fun getPlatform(): Platform = DesktopPlatform()

@Composable
actual fun platformColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme? {
    return null
}
