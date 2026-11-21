package mobi.librera5.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobi.librera5.theme.EInkBackground
import mobi.librera5.theme.EInkOnBackground
import mobi.librera5.theme.SepiaBackground
import mobi.librera5.theme.SepiaOnBackground
import mobi.librera5.ui.components.Shapes
import mobi.librera5.ui.components.Spacing
import mobi.librera5.ui.viewmodel.ReaderUiState
import mobi.librera5.ui.viewmodel.ReaderViewModel
import mobi.librera5.ui.viewmodel.ReadingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    uiState: ReaderUiState,
    viewModel: ReaderViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val book = uiState.book
    val (bgColor, textColor) = when (uiState.readingTheme) {
        ReadingTheme.PAPER -> MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
        ReadingTheme.SEPIA -> SepiaBackground to SepiaOnBackground
        ReadingTheme.NIGHT -> Color(0xFF1E1E1E) to Color(0xFFE0E0E0)
        ReadingTheme.EINK -> EInkBackground to EInkOnBackground
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Book Text Content Surface
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { viewModel.toggleControls() }
                )
                .padding(horizontal = Spacing.lg, vertical = Spacing.md)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            Text(
                text = book?.title ?: "Document Reader",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = textColor,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (book != null && book.author.isNotEmpty()) {
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = textColor.copy(alpha = 0.8f)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs, bottom = Spacing.lg)
                )
            }

            Text(
                text = getSampleChapterText(uiState.currentPage),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = uiState.fontSizeSp.sp,
                    lineHeight = (uiState.fontSizeSp * uiState.lineSpacingMultiplier).sp,
                    color = textColor,
                    fontFamily = FontFamily.Serif
                ),
                textAlign = TextAlign.Justify,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(96.dp))
        }

        // Top App Bar Controls
        AnimatedVisibility(
            visible = uiState.showControls,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = book?.title ?: "Reader",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(Spacing.minTouchTarget)
                            .semantics { contentDescription = "Back to library" }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleBookmark() },
                        modifier = Modifier
                            .size(Spacing.minTouchTarget)
                            .semantics {
                                contentDescription = if (uiState.isBookmarked) "Remove bookmark" else "Bookmark current page"
                            }
                    ) {
                        Icon(
                            imageVector = if (uiState.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = if (uiState.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }

        // Bottom Controls Overlay
        AnimatedVisibility(
            visible = uiState.showControls,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 6.dp,
                shape = Shapes.card
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.md)
                ) {
                    // Page Navigation & Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalIconButton(
                            onClick = { viewModel.prevPage() },
                            modifier = Modifier
                                .size(Spacing.minTouchTarget)
                                .semantics { contentDescription = "Previous page" }
                        ) {
                            Icon(
                                imageVector = Icons.Default.NavigateBefore,
                                contentDescription = null
                            )
                        }

                        Slider(
                            value = uiState.currentPage.toFloat(),
                            onValueChange = { viewModel.goToPage(it.toInt()) },
                            valueRange = 1f..uiState.totalPages.toFloat().coerceAtLeast(1f),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = Spacing.sm)
                                .semantics {
                                    contentDescription = "Page progress slider, page ${uiState.currentPage} of ${uiState.totalPages}"
                                }
                        )

                        FilledTonalIconButton(
                            onClick = { viewModel.nextPage() },
                            modifier = Modifier
                                .size(Spacing.minTouchTarget)
                                .semantics { contentDescription = "Next page" }
                        ) {
                            Icon(
                                imageVector = Icons.Default.NavigateNext,
                                contentDescription = null
                            )
                        }
                    }

                    // Page progress label
                    Text(
                        text = "Page ${uiState.currentPage} of ${uiState.totalPages} (${(uiState.progress * 100).toInt()}%)",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    // Font Size and Reading Theme
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Font controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            FilledTonalIconButton(
                                onClick = { viewModel.decreaseFontSize() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .semantics { contentDescription = "Decrease text font size" }
                            ) {
                                Text("-", fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "${uiState.fontSizeSp.toInt()}sp",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = Spacing.sm)
                            )
                            FilledTonalIconButton(
                                onClick = { viewModel.increaseFontSize() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .semantics { contentDescription = "Increase text font size" }
                            ) {
                                Text("+", fontWeight = FontWeight.Bold)
                            }
                        }

                        // Theme selector chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            ReadingTheme.values().forEach { theme ->
                                FilterChip(
                                    selected = uiState.readingTheme == theme,
                                    onClick = { viewModel.setReadingTheme(theme) },
                                    label = { Text(theme.title, fontSize = 11.sp) },
                                    shape = Shapes.chip,
                                    modifier = Modifier.semantics {
                                        contentDescription = "Switch to ${theme.title} reading theme"
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getSampleChapterText(page: Int): String {
    return """
        Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do: once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, 'and what is the use of a book,' thought Alice 'without pictures or conversations?'

        So she was considering in her own mind (as well as she could, for the hot day made her feel very sleepy and stupid), whether the pleasure of making a daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly a White Rabbit with pink eyes ran close by her.

        There was nothing so very remarkable in that; nor did Alice think it so very much out of the way to hear the Rabbit say to itself, 'Oh dear! Oh dear! I shall be late!' (when she thought it over afterwards, it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural); but when the Rabbit actually took a watch out of its waistcoat-pocket, and looked at it, and then hurried on, Alice started to her feet, for it flashed across her mind that she had never before seen a rabbit with either a waistcoat-pocket, or a watch to take out of it.

        Burning with curiosity, she ran across the field after it, and fortunately was just in time to see it pop down a large rabbit-hole under the hedge. In another moment down went Alice after it, never once considering in the world how she was to get out again.
    """.trimIndent()
}
