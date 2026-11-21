package mobi.librera5.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobi.librera5.model.Book
import mobi.librera5.model.BookFormat

enum class ShelfCategory(val title: String) {
    ALL("All Books"),
    FAVORITES("Favorites"),
    READING("Currently Reading"),
    FINISHED("Finished")
}

enum class SortOrder(val label: String) {
    RECENT("Recently Read"),
    TITLE("Title"),
    AUTHOR("Author"),
    PROGRESS("Progress")
}

data class LibraryUiState(
    val books: List<Book> = emptyList(),
    val searchQuery: String = "",
    val selectedShelf: ShelfCategory = ShelfCategory.ALL,
    val selectedFormat: BookFormat? = null,
    val sortOrder: SortOrder = SortOrder.RECENT,
    val isLoading: Boolean = false,
    val activeBook: Book? = null
) {
    val filteredBooks: List<Book>
        get() {
            var list = books

            if (searchQuery.isNotBlank()) {
                val q = searchQuery.trim().lowercase()
                list = list.filter {
                    it.title.lowercase().contains(q) || it.author.lowercase().contains(q)
                }
            }

            list = when (selectedShelf) {
                ShelfCategory.ALL -> list
                ShelfCategory.FAVORITES -> list.filter { it.isFavorite }
                ShelfCategory.READING -> list.filter { it.progress in 0.01f..0.99f }
                ShelfCategory.FINISHED -> list.filter { it.isRead || it.progress >= 1.0f }
            }

            if (selectedFormat != null) {
                list = list.filter { it.format == selectedFormat }
            }

            return when (sortOrder) {
                SortOrder.RECENT -> list.sortedByDescending { it.lastReadTimestamp }
                SortOrder.TITLE -> list.sortedBy { it.title.lowercase() }
                SortOrder.AUTHOR -> list.sortedBy { it.author.lowercase() }
                SortOrder.PROGRESS -> list.sortedByDescending { it.progress }
            }
        }
}

class LibraryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryUiState(books = sampleBooks))
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectShelf(shelf: ShelfCategory) {
        _uiState.update { it.copy(selectedShelf = shelf) }
    }

    fun selectFormat(format: BookFormat?) {
        _uiState.update { it.copy(selectedFormat = format) }
    }

    fun selectSortOrder(order: SortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
    }

    fun toggleFavorite(bookId: String) {
        _uiState.update { state ->
            val updated = state.books.map {
                if (it.id == bookId) it.copy(isFavorite = !it.isFavorite) else it
            }
            state.copy(books = updated)
        }
    }

    fun markAsRead(bookId: String) {
        _uiState.update { state ->
            val updated = state.books.map {
                if (it.id == bookId) it.copy(isRead = true, progress = 1.0f) else it
            }
            state.copy(books = updated)
        }
    }

    fun openBook(book: Book) {
        _uiState.update { it.copy(activeBook = book) }
    }

    fun closeBook() {
        _uiState.update { it.copy(activeBook = null) }
    }

    fun addBookFromPath(path: String) {
        viewModelScope.launch {
            val name = path.substringAfterLast('/').substringAfterLast('\\')
            val ext = name.substringAfterLast('.', "epub").lowercase()
            val format = BookFormat.values().find { it.extension == ext } ?: BookFormat.EPUB
            val title = name.substringBeforeLast('.')
            val newBook = Book(
                id = "book_${System.currentTimeMillis()}",
                title = title.replace('_', ' ').replace('-', ' '),
                author = "Unknown Author",
                format = format,
                filePath = path,
                progress = 0f,
                currentPage = 1,
                totalPages = 150,
                lastReadTimestamp = System.currentTimeMillis()
            )
            _uiState.update { it.copy(books = listOf(newBook) + it.books) }
        }
    }

    companion object {
        val sampleBooks = listOf(
            Book(
                id = "1",
                title = "Alice's Adventures in Wonderland",
                author = "Lewis Carroll",
                format = BookFormat.EPUB,
                filePath = "assets/books/alicesadventures.epub",
                progress = 0.45f,
                currentPage = 45,
                totalPages = 100,
                isFavorite = true,
                lastReadTimestamp = System.currentTimeMillis() - 3600_000,
                description = "Classic fantasy story following Alice down a rabbit hole into a subterranean wonderland."
            ),
            Book(
                id = "2",
                title = "Pride and Prejudice",
                author = "Jane Austen",
                format = BookFormat.EPUB,
                filePath = "books/pride_and_prejudice.epub",
                progress = 0.82f,
                currentPage = 210,
                totalPages = 256,
                isFavorite = true,
                lastReadTimestamp = System.currentTimeMillis() - 86400_000,
                description = "Romantic novel of manners following Elizabeth Bennet and Fitzwilliam Darcy."
            ),
            Book(
                id = "3",
                title = "Metamorphosis",
                author = "Franz Kafka",
                format = BookFormat.PDF,
                filePath = "books/metamorphosis.pdf",
                progress = 1.0f,
                currentPage = 55,
                totalPages = 55,
                isRead = true,
                lastReadTimestamp = System.currentTimeMillis() - 172800_000,
                description = "Gregor Samsa wakes one morning transformed into a monstrous insect-like creature."
            ),
            Book(
                id = "4",
                title = "Moby Dick",
                author = "Herman Melville",
                format = BookFormat.MOBI,
                filePath = "books/moby_dick.mobi",
                progress = 0.12f,
                currentPage = 60,
                totalPages = 500,
                isFavorite = false,
                lastReadTimestamp = System.currentTimeMillis() - 604800_000,
                description = "Sailor Ishmael's narrative of the obsessive quest of Ahab, captain of the whaling ship Pequod."
            ),
            Book(
                id = "5",
                title = "The War of the Worlds",
                author = "H. G. Wells",
                format = BookFormat.FB2,
                filePath = "books/war_of_the_worlds.fb2",
                progress = 0f,
                currentPage = 1,
                totalPages = 192,
                isFavorite = false,
                lastReadTimestamp = 0L,
                description = "Science fiction masterpiece detailing conflict between mankind and an extraterrestrial race."
            )
        )
    }
}
