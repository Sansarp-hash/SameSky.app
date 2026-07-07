package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.repository.SameSkyRepository
import com.example.api.AdminMetricsDto
import com.example.api.UserProfileDto
import com.example.api.ContentEntryDto
import com.example.api.SupabaseSeriesDto
import com.example.api.SupabaseApiClient

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = SameSkyRepository(db)

    private val _supabaseSeries = MutableStateFlow<List<SupabaseSeriesDto>>(emptyList())
    val supabaseSeries = _supabaseSeries.asStateFlow()

    private val _isFetchingSupabase = MutableStateFlow(false)
    val isFetchingSupabase = _isFetchingSupabase.asStateFlow()

    private val _supabaseError = MutableStateFlow<String?>(null)
    val supabaseError = _supabaseError.asStateFlow()

    private val _adminMetrics = MutableStateFlow<AdminMetricsDto?>(null)
    val adminMetrics = _adminMetrics.asStateFlow()

    private val _userDashboard = MutableStateFlow<UserProfileDto?>(null)
    val userDashboard = _userDashboard.asStateFlow()

    private val _contentEntries = MutableStateFlow<List<ContentEntryDto>>(emptyList())
    val contentEntries = _contentEntries.asStateFlow()

    fun fetchAdminMetrics() {
        viewModelScope.launch {
            val metrics = repository.getAdminMetrics()
            _adminMetrics.value = metrics
        }
    }

    fun fetchUserDashboard() {
        viewModelScope.launch {
            val profile = repository.getUserDashboard()
            _userDashboard.value = profile
        }
    }

    fun enterRaffle(raffleId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val res = repository.enterRaffle(raffleId)
            if (res?.success == true) {
                fetchUserDashboard()
                onSuccess()
            }
        }
    }

    fun fetchContentEntries(category: String? = null) {
        viewModelScope.launch {
            val entries = repository.getContentEntries(category)
            _contentEntries.value = entries
        }
    }

    fun bulkDeleteContentEntries(ids: List<String>) {
        viewModelScope.launch {
            val res = repository.bulkDeleteContentEntries(ids)
            if (res?.success == true) {
                fetchContentEntries() // Reload list
            }
        }
    }

    fun flagContentEntry(id: String, flagged: Boolean, reason: String?) {
        viewModelScope.launch {
            val res = repository.flagContentEntry(id, flagged, reason)
            if (res?.success == true) {
                fetchContentEntries() // Reload list
            }
        }
    }

    private val _currentPageOffset = MutableStateFlow(0)
    val currentPageOffset = _currentPageOffset.asStateFlow()

    private val _hasMoreSupabaseItems = MutableStateFlow(true)
    val hasMoreSupabaseItems = _hasMoreSupabaseItems.asStateFlow()

    private val _isCreatingSeries = MutableStateFlow(false)
    val isCreatingSeries = _isCreatingSeries.asStateFlow()

    private val _createSeriesError = MutableStateFlow<String?>(null)
    val createSeriesError = _createSeriesError.asStateFlow()

    val PAGE_SIZE = 6

    fun createContentEntry(title: String, content: String, category: String, mbti: String?, sunSign: String?) {
        viewModelScope.launch {
            repository.createContentEntry(title, content, category, mbti, sunSign)
            fetchContentEntries() // Reload list
        }
    }

    fun fetchSupabaseSeries(reset: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                _currentPageOffset.value = 0
                _hasMoreSupabaseItems.value = true
                _supabaseSeries.value = emptyList()
            }

            if (!_hasMoreSupabaseItems.value && !reset) {
                return@launch
            }

            _isFetchingSupabase.value = true
            _supabaseError.value = null
            try {
                val apiKey = SupabaseApiClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                val offset = _currentPageOffset.value
                val response = SupabaseApiClient.service.getSeries(
                    apiKey = apiKey,
                    authHeader = authHeader,
                    limit = PAGE_SIZE,
                    offset = offset
                )

                if (response.size < PAGE_SIZE) {
                    _hasMoreSupabaseItems.value = false
                } else {
                    _hasMoreSupabaseItems.value = true
                }

                if (reset) {
                    _supabaseSeries.value = response
                } else {
                    val currentList = _supabaseSeries.value.toMutableList()
                    response.forEach { item ->
                        if (currentList.none { it.id == item.id || (it.title == item.title && it.description == item.description) }) {
                            currentList.add(item)
                        }
                    }
                    _supabaseSeries.value = currentList
                }
                
                _currentPageOffset.value = _currentPageOffset.value + response.size
            } catch (e: Exception) {
                e.printStackTrace()
                _supabaseError.value = "Failed to load series data: ${e.localizedMessage ?: "Connection timed out"}. Please check your connection or try again."
            } finally {
                _isFetchingSupabase.value = false
            }
        }
    }

    fun uploadSupabaseSeries(title: String, description: String, imageUrl: String, priority: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isCreatingSeries.value = true
            _createSeriesError.value = null
            try {
                val apiKey = SupabaseApiClient.getSupabaseKey()
                val authHeader = "Bearer $apiKey"
                val newItem = SupabaseSeriesDto(
                    title = title,
                    description = description,
                    image_url = imageUrl,
                    priority = priority,
                    category = "Thai Series"
                )
                SupabaseApiClient.service.createSeries(
                    apiKey = apiKey,
                    authHeader = authHeader,
                    body = newItem
                )
                
                fetchSupabaseSeries(reset = true)
                onComplete(true)
            } catch (e: Exception) {
                e.printStackTrace()
                _createSeriesError.value = "Failed to upload series: ${e.localizedMessage ?: "Unknown error occurred"}"
                onComplete(false)
            } finally {
                _isCreatingSeries.value = false
            }
        }
    }

    // Movie Search & Watchlist States
    private val _movieSearchResults = MutableStateFlow<List<com.example.api.TmdbMovieDto>>(emptyList())
    val movieSearchResults = _movieSearchResults.asStateFlow()

    private val _watchlistItems = MutableStateFlow<List<com.example.api.WatchlistItemDto>>(emptyList())
    val watchlistItems = _watchlistItems.asStateFlow()

    private val _isSearchingMovies = MutableStateFlow(false)
    val isSearchingMovies = _isSearchingMovies.asStateFlow()

    private val _isFetchingWatchlist = MutableStateFlow(false)
    val isFetchingWatchlist = _isFetchingWatchlist.asStateFlow()

    private val _movieError = MutableStateFlow<String?>(null)
    val movieError = _movieError.asStateFlow()

    private val _watchlistError = MutableStateFlow<String?>(null)
    val watchlistError = _watchlistError.asStateFlow()

    // Persistent Tab and Query states for instant client-side caching
    private val _movieSearchQuery = MutableStateFlow("")
    val movieSearchQuery = _movieSearchQuery.asStateFlow()

    private val _movieActiveTab = MutableStateFlow(0) // 0 = Search Movies, 1 = My Watchlist
    val movieActiveTab = _movieActiveTab.asStateFlow()

    private val _movieSearchPage = MutableStateFlow(1)
    val movieSearchPage = _movieSearchPage.asStateFlow()

    private val _movieTotalPages = MutableStateFlow(1)
    val movieTotalPages = _movieTotalPages.asStateFlow()

    fun setMovieSearchQuery(query: String) {
        _movieSearchQuery.value = query
    }

    fun setMovieActiveTab(tab: Int) {
        _movieActiveTab.value = tab
    }

    fun searchMovies(query: String, page: Int = 1) {
        _movieSearchQuery.value = query
        _movieSearchPage.value = page
        if (query.isBlank()) {
            _movieSearchResults.value = emptyList()
            _movieTotalPages.value = 1
            return
        }
        viewModelScope.launch {
            _isSearchingMovies.value = true
            _movieError.value = null
            try {
                val response = repository.searchMovies(query, page)
                if (response != null) {
                    _movieSearchResults.value = response.results
                    _movieSearchPage.value = response.page ?: page
                    _movieTotalPages.value = response.total_pages ?: 1
                } else {
                    _movieSearchResults.value = emptyList()
                    _movieTotalPages.value = 1
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _movieError.value = "Failed to search movies: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isSearchingMovies.value = false
            }
        }
    }

    fun fetchWatchlist() {
        viewModelScope.launch {
            _isFetchingWatchlist.value = true
            _watchlistError.value = null
            try {
                val items = repository.getWatchlist()
                _watchlistItems.value = items
            } catch (e: Exception) {
                e.printStackTrace()
                _watchlistError.value = "Failed to fetch watchlist: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isFetchingWatchlist.value = false
            }
        }
    }

    fun addToWatchlist(tmdbId: Int, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            try {
                val res = repository.addToWatchlist(tmdbId)
                if (res != null) {
                    fetchWatchlist() // Refresh watchlist
                    onResult(true, res.message ?: "Successfully added to watchlist!")
                } else {
                    onResult(false, "Could not add to watchlist.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, "Error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    fun removeFromWatchlist(tmdbId: Int, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            try {
                val res = repository.removeFromWatchlist(tmdbId)
                if (res != null) {
                    fetchWatchlist() // Refresh watchlist
                    onResult(true, "Successfully removed from watchlist!")
                } else {
                    onResult(false, "Could not remove from watchlist.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, "Error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }
}
