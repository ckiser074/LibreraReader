package mobi.librera5.platform

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class FolderPicker(
    private val onPickFolder: ((String?) -> Unit) -> Unit,
    private val onPickBook: ((String?) -> Unit) -> Unit
) {
    actual fun pickFolder(onFolderSelected: (String?) -> Unit) {
        onPickFolder(onFolderSelected)
    }

    actual fun pickBook(onBookSelected: (String?) -> Unit) {
        onPickBook(onBookSelected)
    }
}

@Composable
actual fun rememberFolderPicker(): FolderPicker {
    var folderCallback: ((String?) -> Unit)? = remember { null }
    var bookCallback: ((String?) -> Unit)? = remember { null }

    val folderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        folderCallback?.invoke(uri?.toString())
    }

    val bookLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        bookCallback?.invoke(uri?.toString())
    }

    return remember {
        FolderPicker(
            onPickFolder = { cb ->
                folderCallback = cb
                folderLauncher.launch(null)
            },
            onPickBook = { cb ->
                bookCallback = cb
                bookLauncher.launch(arrayOf("*/*"))
            }
        )
    }
}
