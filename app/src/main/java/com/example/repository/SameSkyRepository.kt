package com.example.repository

import com.example.AppDatabase
import com.example.UserProfile
import com.example.TarotReading
import com.example.api.SameSkyApiClient
import com.example.api.MbtiRequest
import com.example.api.AstrologyRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SameSkyRepository(private val db: AppDatabase) {
    private val api = SameSkyApiClient.service
    private val dao = db.profileDao()

    // Assuming we have a mock token and userId for this prototype
    private val token = "Bearer mock-jwt-token"
    private val userId = "mock-user-123"

    suspend fun syncProfile() = withContext(Dispatchers.IO) {
        try {
            val mbtiRes = api.getMbti(token, userId)
            val astroRes = api.getAstrology(token, userId)
            
            val currentProfile = dao.getProfile().firstOrNull() ?: UserProfile()
            dao.saveProfile(
                currentProfile.copy(
                    mbtiResult = mbtiRes.result ?: currentProfile.mbtiResult,
                    sunSign = astroRes.sunSign ?: currentProfile.sunSign,
                    moonSign = astroRes.moonSign ?: currentProfile.moonSign,
                    risingSign = astroRes.risingSign ?: currentProfile.risingSign
                )
            )
        } catch (e: Exception) {
            // Log error, continue with local data
            e.printStackTrace()
        }
    }

    suspend fun updateMbti(result: String) = withContext(Dispatchers.IO) {
        try {
            val res = api.updateMbti(token, userId, MbtiRequest(result))
            val currentProfile = dao.getProfile().firstOrNull() ?: UserProfile()
            dao.saveProfile(currentProfile.copy(mbtiResult = res.result))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateAstrology(sun: String?, moon: String?, rising: String?) = withContext(Dispatchers.IO) {
        try {
            val res = api.updateAstrology(token, userId, AstrologyRequest(sun, moon, rising))
            val currentProfile = dao.getProfile().firstOrNull() ?: UserProfile()
            dao.saveProfile(
                currentProfile.copy(
                    sunSign = res.sunSign,
                    moonSign = res.moonSign,
                    risingSign = res.risingSign
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun drawTarot() = withContext(Dispatchers.IO) {
        try {
            val res = api.drawTarotCards(token, userId)
            dao.saveTarotReading(TarotReading(cards = res.cards, reading = res.reading))
            res
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getCommunityPosts() = withContext(Dispatchers.IO) {
        try {
            api.getPosts(token)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getAdminMetrics() = withContext(Dispatchers.IO) {
        try {
            api.getAdminMetrics(token)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getContentEntries(category: String? = null) = withContext(Dispatchers.IO) {
        try {
            api.getContentEntries(token, category)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun bulkDeleteContentEntries(ids: List<String>) = withContext(Dispatchers.IO) {
        try {
            api.bulkDeleteContentEntries(token, com.example.api.BulkDeleteRequest(ids))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun flagContentEntry(id: String, flagged: Boolean, reason: String?) = withContext(Dispatchers.IO) {
        try {
            api.flagContentEntry(token, com.example.api.FlagRequest(id, flagged, reason))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun createContentEntry(title: String, content: String, category: String, mbti: String?, sunSign: String?) = withContext(Dispatchers.IO) {
        try {
            api.createContentEntry(token, com.example.api.CreateContentEntryRequest(title, content, null, category, mbti, sunSign))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserDashboard() = withContext(Dispatchers.IO) {
        try {
            api.getUserProfile(token)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun enterRaffle(raffleId: String) = withContext(Dispatchers.IO) {
        try {
            api.enterRaffle(token, com.example.api.EnterRaffleRequest(raffleId))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getPostDetails(postId: String) = withContext(Dispatchers.IO) {
        try {
            api.getPostDetails(token, postId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun searchMovies(query: String, page: Int = 1) = withContext(Dispatchers.IO) {
        try {
            api.searchMovies(token, query, page)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addToWatchlist(tmdbId: Int) = withContext(Dispatchers.IO) {
        try {
            api.addToWatchlist(token, com.example.api.WatchlistRequest(tmdbId))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getWatchlist() = withContext(Dispatchers.IO) {
        try {
            api.getWatchlist(token).watchlist
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun removeFromWatchlist(tmdbId: Int) = withContext(Dispatchers.IO) {
        try {
            api.removeFromWatchlist(token, com.example.api.WatchlistRequest(tmdbId))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
