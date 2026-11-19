package mobi.librera5.platform

import androidx.compose.runtime.Composable

expect class FolderPicker {
    fun pickFolder(onFolderSelected: (String?) -> Unit)
    fun pickBook(onBookSelected: (String?) -> Unit)
}

@Composable
expect fun rememberFolderPicker(): FolderPicker
