package mobi.librera5.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.swing.JFileChooser
import javax.swing.SwingUtilities

actual class FolderPicker {
    actual fun pickFolder(onFolderSelected: (String?) -> Unit) {
        SwingUtilities.invokeLater {
            val chooser = JFileChooser().apply {
                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                dialogTitle = "Select Book Directory"
            }
            val result = chooser.showOpenDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                onFolderSelected(chooser.selectedFile?.absolutePath)
            } else {
                onFolderSelected(null)
            }
        }
    }

    actual fun pickBook(onBookSelected: (String?) -> Unit) {
        SwingUtilities.invokeLater {
            val chooser = JFileChooser().apply {
                fileSelectionMode = JFileChooser.FILES_ONLY
                dialogTitle = "Select Book to Open"
            }
            val result = chooser.showOpenDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                onBookSelected(chooser.selectedFile?.absolutePath)
            } else {
                onBookSelected(null)
            }
        }
    }
}

@Composable
actual fun rememberFolderPicker(): FolderPicker {
    return remember { FolderPicker() }
}
