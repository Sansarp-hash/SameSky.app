package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.DashboardViewModel
import com.example.api.TmdbMovieDto
import com.example.api.WatchlistItemDto
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieWatchlistScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = viewModel(
        viewModelStoreOwner = androidx.activity.compose.LocalActivity.current as androidx.activity.ComponentActivity
    )
) {
    val searchResults by viewModel.movieSearchResults.collectAsState()
    val watchlistItems by viewModel.watchlistItems.collectAsState()
    val isSearching by viewModel.isSearchingMovies.collectAsState()
    val isFetchingWatchlist by viewModel.isFetchingWatchlist.collectAsState()
    val searchError by viewModel.movieError.collectAsState()
    val watchlistError by viewModel.watchlistError.collectAsState()

    val searchQuery by viewModel.movieSearchQuery.collectAsState()
    val activeTab by viewModel.movieActiveTab.collectAsState()
    val searchPage by viewModel.movieSearchPage.collectAsState()
    val totalPages by viewModel.movieTotalPages.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Fetch watchlist on launch
    LaunchedEffect(Unit) {
        viewModel.fetchWatchlist()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ShimmeringGold
                        )
                    }
                },
                title = {
                    Text(
                        text = "TMDb Watchlist Hub",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Obsidian)
            )
        },
        containerColor = Obsidian
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector Row
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = WarmObsidian,
                contentColor = ShimmeringGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = ShimmeringGold
                    )
                }
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { viewModel.setMovieActiveTab(0) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Search TMDb", fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = ShimmeringGold,
                    unselectedContentColor = MutedText
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { viewModel.setMovieActiveTab(1) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                "My Watchlist (${watchlistItems.size})",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    selectedContentColor = ShimmeringGold,
                    unselectedContentColor = MutedText
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (activeTab) {
                0 -> {
                    // Search Tab
                    SearchMoviesSubView(
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.setMovieSearchQuery(it) },
                        searchResults = searchResults,
                        isSearching = isSearching,
                        searchError = searchError,
                        watchlistItems = watchlistItems,
                        searchPage = searchPage,
                        totalPages = totalPages,
                        onPageChange = { page -> viewModel.searchMovies(searchQuery, page) },
                        onSearchTrigger = { viewModel.searchMovies(searchQuery, 1) },
                        onAddToWatchlist = { movie ->
                            viewModel.addToWatchlist(movie.tmdb_id) { success, message ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        },
                        onRemoveFromWatchlist = { movie ->
                            viewModel.removeFromWatchlist(movie.tmdb_id) { success, message ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        }
                    )
                }
                1 -> {
                    // Watchlist Tab
                    MyWatchlistSubView(
                        watchlistItems = watchlistItems,
                        isFetching = isFetchingWatchlist,
                        watchlistError = watchlistError,
                        onRemove = { movie ->
                            viewModel.removeFromWatchlist(movie.tmdb_id) { success, message ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        },
                        onExploreClick = { viewModel.setMovieActiveTab(0) }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchMoviesSubView(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<TmdbMovieDto>,
    isSearching: Boolean,
    searchError: String?,
    watchlistItems: List<WatchlistItemDto>,
    searchPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    onSearchTrigger: () -> Unit,
    onAddToWatchlist: (TmdbMovieDto) -> Unit,
    onRemoveFromWatchlist: (TmdbMovieDto) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search Input Component
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("movie_search_input"),
            placeholder = { Text("Search for movies on TMDb...", color = MutedText) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = ShimmeringGold) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear", tint = MutedText)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearchTrigger()
                keyboardController?.hide()
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ShimmeringGold,
                unfocusedBorderColor = WarmObsidian,
                focusedTextColor = LightText,
                unfocusedTextColor = LightText,
                focusedContainerColor = WarmObsidian,
                unfocusedContainerColor = WarmObsidian
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                onSearchTrigger()
                keyboardController?.hide()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("movie_search_submit_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Search Movies", color = Obsidian, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // State displays
        if (isSearching) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(6) {
                    MovieGridCardSkeleton()
                }
            }
        } else if (!searchError.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = searchError,
                    color = Color.Red,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        } else if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MovieFilter,
                        contentDescription = null,
                        tint = MutedText.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = if (searchQuery.isBlank()) "Type a movie title to search TMDb API!" else "No movies found for \"$searchQuery\"",
                        color = MutedText,
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            // Results Grid with elegant SWR cache indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Results from TMDb (${searchResults.size})",
                    color = ShimmeringGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                // Cache indicator badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .background(ShimmeringGold.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.FlashOn,
                        contentDescription = null,
                        tint = ShimmeringGold,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "SWR Cached",
                        color = ShimmeringGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(searchResults) { movie ->
                        val isBookmarked = watchlistItems.any { it.tmdb_id == movie.tmdb_id }
                        MovieGridCard(
                            movie = movie,
                            isBookmarked = isBookmarked,
                            onBookmarkClick = {
                                if (isBookmarked) {
                                    onRemoveFromWatchlist(movie)
                                } else {
                                    onAddToWatchlist(movie)
                                }
                            }
                        )
                    }
                }

                // Server-side Pagination controls
                if (totalPages > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onPageChange(searchPage - 1) },
                            enabled = searchPage > 1,
                            modifier = Modifier
                                .background(if (searchPage > 1) ShimmeringGold else WarmObsidian, CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Page",
                                tint = if (searchPage > 1) Obsidian else MutedText
                            )
                        }

                        Text(
                            text = "Page $searchPage of $totalPages",
                            color = LightText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = { onPageChange(searchPage + 1) },
                            enabled = searchPage < totalPages,
                            modifier = Modifier
                                .background(if (searchPage < totalPages) ShimmeringGold else WarmObsidian, CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Page",
                                tint = if (searchPage < totalPages) Obsidian else MutedText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyWatchlistSubView(
    watchlistItems: List<WatchlistItemDto>,
    isFetching: Boolean,
    watchlistError: String?,
    onRemove: (TmdbMovieDto) -> Unit,
    onExploreClick: () -> Unit
) {
    if (isFetching) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(4) {
                MovieGridCardSkeleton()
            }
        }
    } else if (!watchlistError.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = watchlistError,
                color = Color.Red,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    } else if (watchlistItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(ShimmeringGold.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.BookmarkBorder,
                        contentDescription = null,
                        tint = ShimmeringGold,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Text(
                    text = "Your movie watchlist is empty!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Add movies from TMDb Search to curate your personal collection.",
                    color = MutedText,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Button(
                    onClick = onExploreClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Search Movies Now", color = Obsidian, fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "My Curated Watchlist (${watchlistItems.size} Movies)",
                color = ShimmeringGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(watchlistItems) { item ->
                    val movie = item.movie ?: TmdbMovieDto(
                        tmdb_id = item.tmdb_id,
                        title = "Movie ID: ${item.tmdb_id}",
                        description = "Metadata pending retrieval...",
                        release_date = null,
                        poster_path = null,
                        backdrop_path = null,
                        vote_average = null
                    )
                    MovieGridCard(
                        movie = movie,
                        isBookmarked = true,
                        onBookmarkClick = { onRemove(movie) }
                    )
                }
            }
        }
    }
}

@Composable
fun MovieGridCard(
    movie: TmdbMovieDto,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
            .testTag("movie_card_${movie.tmdb_id}"),
        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                // Movie Poster Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    if (movie.poster_path != null) {
                        AsyncImage(
                            model = movie.poster_path,
                            contentDescription = movie.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Movie,
                                contentDescription = null,
                                tint = MutedText.copy(alpha = 0.3f),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    // Vote Average Badge
                    if (movie.vote_average != null && movie.vote_average > 0.0) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .background(Obsidian.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating",
                                tint = ShimmeringGold,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", movie.vote_average),
                                color = ShimmeringGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Title and details
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = movie.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (!movie.release_date.isNullOrBlank()) {
                        Text(
                            text = movie.release_date.substringBefore("-"),
                            color = ShimmeringGold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = movie.description ?: "No description available.",
                        color = MutedText,
                        fontSize = 10.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 14.sp
                    )
                }
            }

            // Floating Action Add/Remove Button
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            if (isBookmarked) DustyRose else Obsidian.copy(alpha = 0.8f),
                            CircleShape
                        )
                        .testTag("bookmark_btn_${movie.tmdb_id}"),
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MovieGridCardSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp),
        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            RomanticSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                cornerRadius = 12.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                RomanticSkeletonBlock(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    height = 14.dp,
                    cornerRadius = 4.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                RomanticSkeletonBlock(
                    modifier = Modifier.fillMaxWidth(0.4f),
                    height = 11.dp,
                    cornerRadius = 4.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                RomanticSkeletonBlock(
                    modifier = Modifier.fillMaxWidth(),
                    height = 10.dp,
                    cornerRadius = 4.dp
                )
                Spacer(modifier = Modifier.height(4.dp))
                RomanticSkeletonBlock(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    height = 10.dp,
                    cornerRadius = 4.dp
                )
            }
        }
    }
}
