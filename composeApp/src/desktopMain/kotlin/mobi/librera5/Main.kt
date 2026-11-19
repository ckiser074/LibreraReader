package mobi.librera5

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication

fun main() = singleWindowApplication(
    title = "Librera Reader",
    state = WindowState(size = DpSize(1024.dp, 768.dp))
) {
    App()
}
