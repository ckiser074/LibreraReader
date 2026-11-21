package mobi.librera5.model

enum class BookFormat(val extension: String, val displayName: String) {
    EPUB("epub", "EPUB"),
    PDF("pdf", "PDF"),
    MOBI("mobi", "MOBI"),
    FB2("fb2", "FB2"),
    CBZ("cbz", "Comic"),
    TXT("txt", "Text")
}

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val format: BookFormat,
    val filePath: String,
    val coverUrl: String? = null,
    val progress: Float = 0f, // 0.0 to 1.0
    val currentPage: Int = 1,
    val totalPages: Int = 100,
    val isFavorite: Boolean = false,
    val isRead: Boolean = false,
    val lastReadTimestamp: Long = 0L,
    val description: String = ""
) {
    val progressPercent: Int
        get() = (progress * 100).toInt().coerceIn(0, 100)
}
