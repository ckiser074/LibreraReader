package mobi.librera5.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

interface Platform {
    val name: String
    val isDesktop: Boolean
    val isAndroid: Boolean
}

expect fun getPlatform(): Platform

@Composable
expect fun platformColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme?
