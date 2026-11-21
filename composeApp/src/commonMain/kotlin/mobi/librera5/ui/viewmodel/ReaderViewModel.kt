package mobi.librera5.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import mobi.librera5.model.Book

enum class ReadingTheme(val title: String) {
    PAPER("Paper"),
    SEPIA("Sepia"),
    NIGHT("Night"),
    EINK("E-Ink")
}

data class ReaderUiState(
    val book: Book? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 100,
    val fontSizeSp: Float = 18f,
    val lineSpacingMultiplier: Float = 1.4f,
    val readingTheme: ReadingTheme = ReadingTheme.PAPER,
    val showControls: Boolean = true,
    val isBookmarked: Boolean = false,
    val chapters: List<String> = listOf("Chapter 1: Down the Rabbit-Hole", "Chapter 2: The Pool of Tears", "Chapter 3: A Caucus-Race", "Chapter 4: The Rabbit Sends in a Little Bill")
) {
    val progress: Float
        get() = if (totalPages > 0) (currentPage.toFloat() / totalPages.toFloat()).coerceIn(0f, 1f) else 0f
}

class ReaderViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    fun loadBook(book: Book) {
        _uiState.update {
            it.copy(
                book = book,
                currentPage = book.currentPage.coerceAtLeast(1),
                totalPages = book.totalPages.coerceAtLeast(1),
                isBookmarked = false
            )
        }
    }

    fun nextPage() {
        _uiState.update {
            val next = (it.currentPage + 1).coerceAtMost(it.totalPages)
            it.copy(currentPage = next)
        }
    }

    fun prevPage() {
        _uiState.update {
            val prev = (it.currentPage - 1).coerceAtLeast(1)
            it.copy(currentPage = prev)
        }
    }

    fun goToPage(page: Int) {
        _uiState.update {
            it.copy(currentPage = page.coerceIn(1, it.totalPages))
        }
    }

    fun toggleControls() {
        _uiState.update { it.copy(showControls = !it.showControls) }
    }

    fun increaseFontSize() {
        _uiState.update { it.copy(fontSizeSp = (it.fontSizeSp + 2f).coerceAtMost(36f)) }
    }

    fun decreaseFontSize() {
        _uiState.update { it.copy(fontSizeSp = (it.fontSizeSp - 2f).coerceAtLeast(12f)) }
    }

    fun setReadingTheme(theme: ReadingTheme) {
        _uiState.update { it.copy(readingTheme = theme) }
    }

    fun toggleBookmark() {
        _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
    }
}
