package mobi.librera5.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import mobi.librera5.model.Book
import mobi.librera5.model.BookFormat
import mobi.librera5.platform.rememberFolderPicker
import mobi.librera5.ui.components.BookItemCard
import mobi.librera5.ui.components.FilterShelfChip
import mobi.librera5.ui.components.LibreraSearchBar
import mobi.librera5.ui.components.Shapes
import mobi.librera5.ui.components.Spacing
import mobi.librera5.ui.viewmodel.LibraryUiState
import mobi.librera5.ui.viewmodel.LibraryViewModel
import mobi.librera5.ui.viewmodel.ShelfCategory
import mobi.librera5.ui.viewmodel.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    viewModel: LibraryViewModel,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    val folderPicker = rememberFolderPicker()
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Librera Reader",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier
                            .size(Spacing.minTouchTarget)
                            .semantics { contentDescription = "Sort library books" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = null
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortOrder.values().forEach { order ->
                            DropdownMenuItem(
                                text = { Text(order.label) },
                                onClick = {
                                    viewModel.selectSortOrder(order)
                                    showSortMenu = false
                                }
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            folderPicker.pickBook { path ->
                                path?.let { viewModel.addBookFromPath(it) }
                            }
                        },
                        modifier = Modifier
                            .size(Spacing.minTouchTarget)
                            .semantics { contentDescription = "Open book from storage" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    folderPicker.pickBook { path ->
                        path?.let { viewModel.addBookFromPath(it) }
                    }
                },
                shape = Shapes.button,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(56.dp)
                    .semantics { contentDescription = "Import new book" }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            LibreraSearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm)
            )

            // Shelf Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                ShelfCategory.values().forEach { shelf ->
                    FilterShelfChip(
                        selected = uiState.selectedShelf == shelf,
                        onClick = { viewModel.selectShelf(shelf) },
                        label = shelf.title
                    )
                }
            }

            // Format Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                FilterShelfChip(
                    selected = uiState.selectedFormat == null,
                    onClick = { viewModel.selectFormat(null) },
                    label = "All Formats"
                )
                BookFormat.values().forEach { fmt ->
                    FilterShelfChip(
                        selected = uiState.selectedFormat == fmt,
                        onClick = { viewModel.selectFormat(fmt) },
                        label = fmt.displayName
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            val books = uiState.filteredBooks
            if (books.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.xl),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = if (uiState.searchQuery.isNotEmpty()) "No books match your search" else "No books found in this shelf",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Button(
                            onClick = {
                                folderPicker.pickBook { path ->
                                    path?.let { viewModel.addBookFromPath(it) }
                                }
                            },
                            shape = Shapes.button
                        ) {
                            Text("Open a Book")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(books, key = { it.id }) { book ->
                        BookItemCard(
                            book = book,
                            onClick = { onBookClick(book) },
                            onFavoriteToggle = { viewModel.toggleFavorite(book.id) }
                        )
                    }
                }
            }
        }
    }
}
