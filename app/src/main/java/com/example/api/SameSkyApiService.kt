package com.example.api
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@JsonClass(generateAdapter = true)
data class MbtiResponse(val result: String?)
@JsonClass(generateAdapter = true)
data class MbtiRequest(val result: String)
@JsonClass(generateAdapter = true)
data class AstrologyResponse(val sunSign: String?, val moonSign: String?, val risingSign: String?)
@JsonClass(generateAdapter = true)
data class AstrologyRequest(val sunSign: String?, val moonSign: String?, val risingSign: String?)
@JsonClass(generateAdapter = true)
data class TarotReadingDto(val id: String, val cards: String, val reading: String)
@JsonClass(generateAdapter = true)
data class PostDto(
    val id: String,
    val content: String,
    val author: AuthorDto?,
    val upvotes: Int,
    val comments: List<CommentDto>? = null
)
@JsonClass(generateAdapter = true)
data class CommentDto(
    val id: String,
    val content: String,
    val author: AuthorDto?,
    val upvotes: Int
)
@JsonClass(generateAdapter = true)
data class AuthorDto(val name: String?, val tier: String? = "Free", val isAdmin: Boolean = false)
@JsonClass(generateAdapter = true)
data class AdminMetricsDto(
    val totalCoinVolume: Double?,
    val subscriptionCount: Int?,
    val raffleParticipationRates: Int?
)
@JsonClass(generateAdapter = true)
data class UserProfileDto(
    val id: String,
    val email: String,
    val name: String?,
    val coinBalance: String?,
    val premiumStatus: Boolean?,
    val loyaltyBadge: String?,
    val activeRaffles: Int?,
    val watchlistCount: Int?
)

@JsonClass(generateAdapter = true)
data class ContentEntryDto(
    val id: String,
    val title: String,
    val content: String,
    val mediaUrl: String? = null,
    val category: String,
    val flagged: Boolean,
    val flagReason: String? = null,
    val mbti: String? = null,
    val sunSign: String? = null,
    val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class BulkDeleteRequest(val ids: List<String>)

@JsonClass(generateAdapter = true)
data class BulkDeleteResponse(val success: Boolean, val count: Int)

@JsonClass(generateAdapter = true)
data class FlagRequest(val id: String, val flagged: Boolean, val flagReason: String?)

@JsonClass(generateAdapter = true)
data class FlagResponse(val success: Boolean)

@JsonClass(generateAdapter = true)
data class CreateContentEntryRequest(
    val title: String,
    val content: String,
    val mediaUrl: String? = null,
    val category: String,
    val mbti: String? = null,
    val sunSign: String? = null
)

@JsonClass(generateAdapter = true)
data class TmdbMovieDto(
    val tmdb_id: Int,
    val title: String,
    val description: String?,
    val release_date: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val vote_average: Double?
)

@JsonClass(generateAdapter = true)
data class TmdbSearchResponse(
    val results: List<TmdbMovieDto>,
    val page: Int?,
    val total_results: Int?,
    val total_pages: Int?
)

@JsonClass(generateAdapter = true)
data class WatchlistRequest(
    val tmdb_id: Int
)

@JsonClass(generateAdapter = true)
data class WatchlistResponse(
    val message: String?,
    val entry: WatchlistEntryDto? = null
)

@JsonClass(generateAdapter = true)
data class WatchlistEntryDto(
    val id: String,
    val user_id: String,
    val tmdb_id: Int,
    val created_at: String?
)

@JsonClass(generateAdapter = true)
data class WatchlistItemDto(
    val id: String,
    val user_id: String,
    val tmdb_id: Int,
    val created_at: String?,
    val movie: TmdbMovieDto?
)

@JsonClass(generateAdapter = true)
data class WatchlistListResponse(
    val watchlist: List<WatchlistItemDto>
)

@JsonClass(generateAdapter = true)
data class EnterRaffleRequest(val raffleId: String)

@JsonClass(generateAdapter = true)
data class EnterRaffleResponse(val success: Boolean)


interface SameSkyApiService {
    @POST("api/user/raffle")
    suspend fun enterRaffle(
        @Header("Authorization") token: String,
        @Body request: EnterRaffleRequest
    ): EnterRaffleResponse

    @GET("api/mbti/{userId}/result")
    suspend fun getMbti(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): MbtiResponse
    @POST("api/mbti/{userId}/result")
    suspend fun updateMbti(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Body request: MbtiRequest
    ): MbtiResponse
    @GET("api/astrology/{userId}")
    suspend fun getAstrology(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): AstrologyResponse
    @POST("api/astrology/{userId}")
    suspend fun updateAstrology(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Body request: AstrologyRequest
    ): AstrologyResponse
    @GET("api/tarot/{userId}")
    suspend fun getTarotReadings(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): List<TarotReadingDto>
    @POST("api/tarot/{userId}/draw")
    suspend fun drawTarotCards(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): TarotReadingDto
    @GET("api/community/posts")
    suspend fun getPosts(
        @Header("Authorization") token: String
    ): List<PostDto>
    @GET("api/community/posts/{postId}")
    suspend fun getPostDetails(
        @Header("Authorization") token: String,
        @Path("postId") postId: String
    ): PostDto
    @GET("api/admin/metrics")
    suspend fun getAdminMetrics(
        @Header("Authorization") token: String
    ): AdminMetricsDto
    @GET("api/user/dashboard")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): UserProfileDto

    @GET("api/admin/content-entries")
    suspend fun getContentEntries(
        @Header("Authorization") token: String,
        @Query("category") category: String? = null
    ): List<ContentEntryDto>

    @POST("api/admin/content-entries/bulk-delete")
    suspend fun bulkDeleteContentEntries(
        @Header("Authorization") token: String,
        @Body request: BulkDeleteRequest
    ): BulkDeleteResponse

    @POST("api/admin/content-entries/flag")
    suspend fun flagContentEntry(
        @Header("Authorization") token: String,
        @Body request: FlagRequest
    ): FlagResponse

    @POST("api/admin/content-entries")
    suspend fun createContentEntry(
        @Header("Authorization") token: String,
        @Body request: CreateContentEntryRequest
    ): ContentEntryDto

    @GET("api/movies/search")
    suspend fun searchMovies(
        @Header("Authorization") token: String,
        @Query("query") query: String,
        @Query("page") page: Int? = 1
    ): TmdbSearchResponse

    @POST("api/movies/watchlist")
    suspend fun addToWatchlist(
        @Header("Authorization") token: String,
        @Body request: WatchlistRequest
    ): WatchlistResponse

    @GET("api/movies/watchlist")
    suspend fun getWatchlist(
        @Header("Authorization") token: String
    ): WatchlistListResponse

    @retrofit2.http.HTTP(method = "DELETE", path = "api/movies/watchlist", hasBody = true)
    suspend fun removeFromWatchlist(
        @Header("Authorization") token: String,
        @Body request: WatchlistRequest
    ): WatchlistResponse
}

object SameSkyApiClient {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val service: SameSkyApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SameSkyApiService::class.java)
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseSeriesDto(
    val id: Long? = null,
    val title: String,
    val description: String,
    val image_url: String,
    val priority: String? = null,
    val category: String? = null,
    val release_year: String? = null,
    val region: String? = null
)

interface SupabaseApiService {
    @GET("rest/v1/series?select=*")
    suspend fun getSeries(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): List<SupabaseSeriesDto>

    @POST("rest/v1/series")
    suspend fun createSeries(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body body: SupabaseSeriesDto
    ): List<SupabaseSeriesDto>
}

object SupabaseApiClient {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private const val FALLBACK_URL = "https://wwmovmqrudnddaqxrzbk.supabase.co/"
    private const val FALLBACK_KEY = "sb_publishable_9NNpeKzFqUYD4OGMi7gZPg__X6zFHrl"

    fun getSupabaseUrl(): String {
        return try {
            val url = com.example.BuildConfig.SUPABASE_URL
            if (url.isNullOrBlank() || url == "YOUR_SUPABASE_URL_HERE") {
                FALLBACK_URL
            } else {
                if (url.endsWith("/")) url else "$url/"
            }
        } catch (e: Exception) {
            FALLBACK_URL
        }
    }

    fun getSupabaseKey(): String {
        return try {
            val key = com.example.BuildConfig.SUPABASE_KEY
            if (key.isNullOrBlank() || key == "YOUR_SUPABASE_KEY_HERE") {
                FALLBACK_KEY
            } else {
                key
            }
        } catch (e: Exception) {
            FALLBACK_KEY
        }
    }

    val service: SupabaseApiService by lazy {
        val baseUrl = getSupabaseUrl()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SupabaseApiService::class.java)
    }
}
