package com.example

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

/**
 * An intersection observer-based lazy loading utility for Jetpack Compose.
 * Prevents application lag when rendering high-resolution content carousels and feed grids
 * by triggering pagination or content loading only when the user scrolls near the end of the list.
 */
@Composable
fun rememberIntersectionObserver(
    lazyListState: LazyListState,
    buffer: Int = 2,
    onLoadMore: () -> Unit
) {
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
            .map { it.lastOrNull()?.index }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { lastIndex ->
                val totalItems = lazyListState.layoutInfo.totalItemsCount
                if (totalItems > 0 && lastIndex >= totalItems - 1 - buffer) {
                    onLoadMore()
                }
            }
    }
}
