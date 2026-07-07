package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.repository.SameSkyRepository
import com.example.api.PostDto
import com.example.api.AuthorDto

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.profileDao()
    private val repository = SameSkyRepository(db)

    val profile = dao.getProfile().stateIn(viewModelScope, SharingStarted.Lazily, null)
    val savedMediaLists = dao.getSavedMediaLists().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val favorites = dao.getFavorites().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun saveMediaList(title: String, description: String, items: List<String>) {
        viewModelScope.launch {
            dao.saveMediaList(
                SavedMediaList(
                    title = title,
                    description = description,
                    itemsJson = items.joinToString(",")
                )
            )
        }
    }

    fun deleteSavedMediaList(id: Int) {
        viewModelScope.launch {
            dao.deleteSavedMediaList(id)
        }
    }

    fun toggleFavorite(media: MediaItem) {
        viewModelScope.launch {
            val isFavorite = favorites.value.any { it.title == media.title }
            if (isFavorite) {
                dao.removeFavorite(media.title)
            } else {
                dao.addFavorite(
                    FavoriteMedia(
                        title = media.title,
                        thumbnail = media.thumbnail,
                        region = media.region,
                        releaseYear = media.releaseYear,
                        description = media.description
                    )
                )
            }
        }
    }
    
    // Astrology State
    private val _dailyHoroscope = MutableStateFlow<String?>(null)
    val dailyHoroscope = _dailyHoroscope.asStateFlow()
    private val _isHoroscopeLoading = MutableStateFlow(false)
    val isHoroscopeLoading = _isHoroscopeLoading.asStateFlow()

    // Tarot State
    private val _drawnCards = MutableStateFlow<List<String>>(emptyList())
    val drawnCards = _drawnCards.asStateFlow()
    private val _tarotReading = MutableStateFlow<String?>(null)
    val tarotReading = _tarotReading.asStateFlow()
    private val _isTarotLoading = MutableStateFlow(false)
    val isTarotLoading = _isTarotLoading.asStateFlow()

    // MBTI State
    private val _mbtiAnswers = MutableStateFlow<Map<Int, String>>(emptyMap())
    val mbtiAnswers = _mbtiAnswers.asStateFlow()

    // Community State
    private val _posts = MutableStateFlow<List<PostDto>>(emptyList())
    val posts = _posts.asStateFlow()
    private val _selectedPost = MutableStateFlow<PostDto?>(null)
    val selectedPost = _selectedPost.asStateFlow()

    init {
        // Sync profile data with backend on initialization
        viewModelScope.launch {
            repository.syncProfile()
            fetchPosts()
        }
    }

    fun updateProfile(
        mbti: String? = null, 
        sunSign: String? = null, 
        moonSign: String? = null, 
        risingSign: String? = null, 
        isDarkMode: Boolean? = null,
        favoriteSeries: String? = null,
        favoriteCouple: String? = null,
        favoriteActress: String? = null,
        bias: String? = null,
        isAdmin: Boolean? = null,
        isModerator: Boolean? = null,
        subscriptionTier: String? = null
    ) {
        viewModelScope.launch {
            if (mbti != null) repository.updateMbti(mbti)
            if (sunSign != null || moonSign != null || risingSign != null) {
                repository.updateAstrology(sunSign, moonSign, risingSign)
            }
            // Local update for immediate UI reflection and settings like isDarkMode
            val current = profile.value ?: UserProfile()
            dao.saveProfile(
                current.copy(
                    mbtiResult = mbti ?: current.mbtiResult,
                    sunSign = sunSign ?: current.sunSign,
                    moonSign = moonSign ?: current.moonSign,
                    risingSign = risingSign ?: current.risingSign,
                    isDarkMode = isDarkMode ?: current.isDarkMode,
                    favoriteSeries = favoriteSeries ?: current.favoriteSeries,
                    favoriteCouple = favoriteCouple ?: current.favoriteCouple,
                    favoriteActress = favoriteActress ?: current.favoriteActress,
                    bias = bias ?: current.bias,
                    isAdmin = isAdmin ?: current.isAdmin,
                    isModerator = isModerator ?: current.isModerator,
                    subscriptionTier = subscriptionTier ?: current.subscriptionTier
                )
            )
        }
    }

    fun fetchHoroscope(sign: String) {
        viewModelScope.launch {
            _isHoroscopeLoading.value = true
            try {
                // Try 3rd party API first (simulated here since AstrologyClient is unchanged)
                val response = AstrologyClient.service.getDailyHoroscope(sign)
                val text = response.data?.horoscopeData
                if (!text.isNullOrEmpty()) {
                    _dailyHoroscope.value = text
                } else {
                    throw Exception("Empty response")
                }
            } catch (e: Exception) {
                // Fallback to Gemini
                val prompt = "Provide a daily horoscope for $sign. Keep it positive and focus on romance and personal growth, tying it into Girls' Love themes if possible."
                val fallbackText = generateContent(prompt)
                if (fallbackText.startsWith("API Key is missing.") || fallbackText.startsWith("Error:") || fallbackText.isBlank()) {
                    _dailyHoroscope.value = generateLocalHoroscope(sign)
                } else {
                    _dailyHoroscope.value = fallbackText
                }
            } finally {
                _isHoroscopeLoading.value = false
            }
        }
    }

    fun generateLocalHoroscope(sign: String): String {
        val romanticHoroscopes = mapOf(
            "Aries" to "Today, your fiery Aries nature matches perfectly with an adventurous partner. A playful spark is waiting to erupt in your social circle. Keep an eye out for long, meaningful glances!",
            "Taurus" to "Comfort and romantic stability are highlighted for you today. A cozy evening sharing favorite GL stories or soft conversations will strengthen a growing bond. Trust the timing of your heart.",
            "Gemini" to "Your playful wit is your greatest charm today! Engage in conversations about your favorite pairings—someone special is listening and falling for your charming intellect.",
            "Cancer" to "Your emotional intuition is peaking today, Cancer. You can sense unspoken feelings easily. Do not be afraid to express your vulnerability; it is your ultimate superpower in love.",
            "Leo" to "Your radiant energy is absolutely magnetic today! The spotlight is on you, and a special admirer is completely mesmerized by your warmth. Embrace your celestial glow.",
            "Virgo" to "Patience and small gestures of love will speak volumes today. Organising a special date or sending a thoughtful text will show how much you care. Actions speak louder than words.",
            "Libra" to "Balance and harmony grace your romantic life today. An unexpected sweet interaction will restore your faith in soulmates. Let yourself be swept away by soft romance.",
            "Scorpio" to "Intense passion and deep emotional mystery surround you today, Scorpio. Secrets are waiting to be unveiled. A lingering touch or intense eye contact will set your heart racing.",
            "Sagittarius" to "Adventure and laughter are your cosmic guides today! Share a spontaneous moment or plan a surprise outing. Love flourishes when you step outside your comfort zone.",
            "Capricorn" to "A deep, serious connection is taking root today. Your reliability is comforting to someone who is secretly looking for a safe harbor. A meaningful commitment is on the horizon.",
            "Aquarius" to "Your unique, independent charm is incredibly captivating today. Someone is deeply drawn to your brilliant mind and cosmic perspectives. Stay true to your wonderful self.",
            "Pisces" to "A dreamlike romance is in the stars for you today, Pisces! Your creative imagination and soft heart will invite beautiful romantic energy. Let your romantic fantasies guide your day."
        )
        return romanticHoroscopes[sign] ?: "The stars today indicate a beautiful romantic energy and deep, soulful connections. Trust your feelings and keep your heart open."
    }

    fun drawTarotCards() {
        viewModelScope.launch {
            _isTarotLoading.value = true
            _tarotReading.value = null
            
            // Draw via new backend
            val result = repository.drawTarot()
            
            if (result != null) {
                _drawnCards.value = result.cards.split(", ")
                _tarotReading.value = result.reading
            } else {
                // Fallback local simulation if backend is unreachable
                val allCards = listOf("The Fool", "The Magician", "The High Priestess", "The Empress", "The Emperor", "The Hierophant", "The Lovers", "The Chariot", "Strength", "The Hermit", "Wheel of Fortune", "Justice", "The Hanged Man", "Death", "Temperance", "The Devil", "The Tower", "The Star", "The Moon", "The Sun", "Judgement", "The World")
                val drawn = allCards.shuffled().take(3)
                _drawnCards.value = drawn
                val cardsText = drawn.joinToString(", ")
                val prompt = "I drew three tarot cards: $cardsText. Provide a short, narrative-driven tarot reading interpreting these cards for my journey as a GL fan. Interpret the past, present, and future."
                val reading = generateContent(prompt)
                
                val finalReading = if (reading.startsWith("API Key is missing.") || reading.startsWith("Error:") || reading.isBlank()) {
                    generateLocalTarotReading(drawn)
                } else {
                    reading
                }
                
                _tarotReading.value = finalReading
                dao.saveTarotReading(TarotReading(cards = cardsText, reading = finalReading))
            }
            
            _isTarotLoading.value = false
        }
    }

    fun generateLocalTarotReading(cards: List<String>): String {
        val cardInterpretations = mapOf(
            "The Fool" to "New romantic journeys, innocent crushes, and taking a leap of faith in love.",
            "The Magician" to "Manifesting deep connections, charismatic charm, and the power to steer your own relationship destiny.",
            "The High Priestess" to "Unspoken desires, hidden chemistry, and intuition that whispers of a secret admirer.",
            "The Empress" to "Abundant romance, nurturing care, and a love that flourishes under gentle attention.",
            "The Emperor" to "Protective energy, solid commitment, and the comfort of a reliable, grounded partner.",
            "The Hierophant" to "Traditional pairings, deep-rooted vows, and finding comfort in shared values and stories.",
            "The Lovers" to "Ultimate cosmic harmony, mutual adoration, and a soulmate connection that transcends stars.",
            "The Chariot" to "Overcoming relationship obstacles, pursuing your affection with determination, and winning a heart.",
            "Strength" to "Gentle patience, emotional resilience, and tamed passions that build an unbreakable bond.",
            "The Hermit" to "Quiet reflection, self-love, and discovering what your heart truly desires before searching elsewhere.",
            "Wheel of Fortune" to "Unexpected romantic encounters, twist of fate, and cosmic timing bringing you together.",
            "Justice" to "Honesty, mutual respect, and a perfectly balanced dynamic where love is returned in equal measure.",
            "The Hanged Man" to "Letting go of old expectations, seeing love from a fresh perspective, and learning to wait for the right moment.",
            "Death" to "The end of an era, letting go of past unrequited love, and welcoming a beautiful rebirth in your heart.",
            "Temperance" to "Peaceful harmony, healing old emotional wounds, and finding a serene balance with your favorite person.",
            "The Devil" to "Intense, magnetic attraction, playful obsession, and a passion that is hard to resist.",
            "The Tower" to "A sudden dramatic revelation, breaking of facades, and rebuilding a stronger, more authentic relationship.",
            "The Star" to "Hope, divine alignment, healing, and absolute faith that your love story is written in the stars.",
            "The Moon" to "Illusion, mysterious dreams, hidden feelings waiting to be expressed under the moonlight.",
            "The Sun" to "Radiant joy, absolute clarity, celebration of love, and a happy ending filled with warmth.",
            "Judgement" to "A call to make a definitive choice, emotional awakening, and embracing your true romantic path.",
            "The World" to "Complete fulfillment, wholeness, and a beautifully completed circle of love and happiness."
        )

        val c1 = cards.getOrNull(0) ?: "The Lovers"
        val c2 = cards.getOrNull(1) ?: "The Star"
        val c3 = cards.getOrNull(2) ?: "The Sun"

        return """
            ✨ PAST: ${cardInterpretations[c1] ?: "A period of emotional growth and cosmic preparation."}
            
            🌌 PRESENT: ${cardInterpretations[c2] ?: "A time of deep romance and discovering hidden connections."}
            
            🔮 FUTURE: ${cardInterpretations[c3] ?: "A beautiful resolution where your heart's truest wishes align."}
            
            💖 COSMIC ADVICE: The universe advises you to trust your intuition. In the world of SameSky, your destiny is intricately linked with genuine connections and raw emotional honesty. Keep your heart open!
        """.trimIndent()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            val backendPosts = repository.getCommunityPosts()
            if (backendPosts.isEmpty()) {
                // Fallback mock posts if backend is unreachable
                _posts.value = listOf(
                    PostDto("1", "Just finished watching the latest episode! So amazing ✨ #GL", AuthorDto("User 1"), 120, null),
                    PostDto("2", "The character development in this series is unmatched.", AuthorDto("User 2"), 85, null),
                    PostDto("3", "Any recommendations for shows similar to Blank The Series?", AuthorDto("User 3"), 45, null)
                )
            } else {
                _posts.value = backendPosts
            }
        }
    }

    fun addPost(content: String, authorName: String = "You") {
        val newPost = PostDto(
            id = java.util.UUID.randomUUID().toString(),
            content = content,
            author = AuthorDto(authorName),
            upvotes = 0,
            comments = emptyList()
        )
        _posts.value = listOf(newPost) + _posts.value
    }

    fun fetchPostDetails(postId: String) {
        viewModelScope.launch {
            val post = repository.getPostDetails(postId)
            if (post == null) {
                // Fallback mock post
                _selectedPost.value = PostDto(
                    postId, 
                    "Just finished watching the latest episode! So amazing ✨ #GL", 
                    AuthorDto("User $postId"), 
                    120, 
                    listOf(
                        com.example.api.CommentDto("c1", "This is a great point!", AuthorDto("Commenter 1"), 12),
                        com.example.api.CommentDto("c2", "I completely agree.", AuthorDto("Commenter 2"), 5)
                    )
                )
            } else {
                _selectedPost.value = post
            }
        }
    }

    fun answerMbtiQuestion(index: Int, answer: String) {
        _mbtiAnswers.value = _mbtiAnswers.value.toMutableMap().apply { put(index, answer) }
    }

    fun calculateMbti() {
        val answers = _mbtiAnswers.value
        if (answers.size < 4) return // Need at least 4 for a simple simulation
        
        var e = 0; var i = 0
        var s = 0; var n = 0
        var t = 0; var f = 0
        var j = 0; var p = 0

        if (answers[0] == "A") e++ else i++
        if (answers[1] == "A") s++ else n++
        if (answers[2] == "A") t++ else f++
        if (answers[3] == "A") j++ else p++

        val result = buildString {
            append(if (e >= i) "E" else "I")
            append(if (s >= n) "S" else "N")
            append(if (t >= f) "T" else "F")
            append(if (j >= p) "J" else "P")
        }
        
        updateProfile(mbti = result)
    }
}
