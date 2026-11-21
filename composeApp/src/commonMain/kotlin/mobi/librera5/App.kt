package mobi.librera5

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import mobi.librera5.platform.getPlatform
import mobi.librera5.theme.LibreraTheme
import mobi.librera5.ui.screens.LibraryScreen
import mobi.librera5.ui.screens.ReaderScreen
import mobi.librera5.ui.screens.SettingsScreen
import mobi.librera5.ui.viewmodel.LibraryViewModel
import mobi.librera5.ui.viewmodel.ReaderViewModel
import mobi.librera5.ui.viewmodel.ShelfCategory

enum class AppDestination(val title: String) {
    LIBRARY("Library"),
    FAVORITES("Favorites"),
    SETTINGS("Settings")
}

@Composable
fun App() {
    val platform = getPlatform()
    val systemDark = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDark) }
    var dynamicColor by remember { mutableStateOf(true) }

    val libraryViewModel: LibraryViewModel = viewModel { LibraryViewModel() }
    val readerViewModel: ReaderViewModel = viewModel { ReaderViewModel() }

    val libraryUiState by libraryViewModel.uiState.collectAsState()
    val readerUiState by readerViewModel.uiState.collectAsState()

    var currentDestination by remember { mutableStateOf(AppDestination.LIBRARY) }

    LibreraTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
        val activeBook = libraryUiState.activeBook

        if (activeBook != null) {
            ReaderScreen(
                uiState = readerUiState,
                viewModel = readerViewModel,
                onBackClick = {
                    libraryViewModel.closeBook()
                }
            )
        } else {
            if (platform.isDesktop) {
                // Desktop Adaptive Layout with NavigationRail
                Row(modifier = Modifier.fillMaxSize()) {
                    NavigationRail {
                        NavigationRailItem(
                            selected = currentDestination == AppDestination.LIBRARY,
                            onClick = {
                                currentDestination = AppDestination.LIBRARY
                                libraryViewModel.selectShelf(ShelfCategory.ALL)
                            },
                            icon = { Icon(Icons.Default.Book, contentDescription = null) },
                            label = { Text("Library") },
                            modifier = Modifier.semantics { contentDescription = "Navigate to Library" }
                        )

                        NavigationRailItem(
                            selected = currentDestination == AppDestination.FAVORITES,
                            onClick = {
                                currentDestination = AppDestination.FAVORITES
                                libraryViewModel.selectShelf(ShelfCategory.FAVORITES)
                            },
                            icon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                            label = { Text("Favorites") },
                            modifier = Modifier.semantics { contentDescription = "Navigate to Favorites" }
                        )

                        NavigationRailItem(
                            selected = currentDestination == AppDestination.SETTINGS,
                            onClick = { currentDestination = AppDestination.SETTINGS },
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            label = { Text("Settings") },
                            modifier = Modifier.semantics { contentDescription = "Navigate to Settings" }
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        when (currentDestination) {
                            AppDestination.LIBRARY, AppDestination.FAVORITES -> {
                                LibraryScreen(
                                    uiState = libraryUiState,
                                    viewModel = libraryViewModel,
                                    onBookClick = { book ->
                                        readerViewModel.loadBook(book)
                                        libraryViewModel.openBook(book)
                                    }
                                )
                            }
                            AppDestination.SETTINGS -> {
                                SettingsScreen(
                                    darkTheme = darkTheme,
                                    onDarkThemeChange = { darkTheme = it },
                                    dynamicColor = dynamicColor,
                                    onDynamicColorChange = { dynamicColor = it }
                                )
                            }
                        }
                    }
                }
            } else {
                // Mobile Layout with Scaffold and NavigationBar
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentDestination == AppDestination.LIBRARY,
                                onClick = {
                                    currentDestination = AppDestination.LIBRARY
                                    libraryViewModel.selectShelf(ShelfCategory.ALL)
                                },
                                icon = { Icon(Icons.Default.Book, contentDescription = null) },
                                label = { Text("Library") },
                                modifier = Modifier.semantics { contentDescription = "Navigate to Library" }
                            )

                            NavigationBarItem(
                                selected = currentDestination == AppDestination.FAVORITES,
                                onClick = {
                                    currentDestination = AppDestination.FAVORITES
                                    libraryViewModel.selectShelf(ShelfCategory.FAVORITES)
                                },
                                icon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                                label = { Text("Favorites") },
                                modifier = Modifier.semantics { contentDescription = "Navigate to Favorites" }
                            )

                            NavigationBarItem(
                                selected = currentDestination == AppDestination.SETTINGS,
                                onClick = { currentDestination = AppDestination.SETTINGS },
                                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                label = { Text("Settings") },
                                modifier = Modifier.semantics { contentDescription = "Navigate to Settings" }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        when (currentDestination) {
                            AppDestination.LIBRARY, AppDestination.FAVORITES -> {
                                LibraryScreen(
                                    uiState = libraryUiState,
                                    viewModel = libraryViewModel,
                                    onBookClick = { book ->
                                        readerViewModel.loadBook(book)
                                        libraryViewModel.openBook(book)
                                    }
                                )
                            }
                            AppDestination.SETTINGS -> {
                                SettingsScreen(
                                    darkTheme = darkTheme,
                                    onDarkThemeChange = { darkTheme = it },
                                    dynamicColor = dynamicColor,
                                    onDynamicColorChange = { dynamicColor = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
