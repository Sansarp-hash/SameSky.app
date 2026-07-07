package com.example

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class Tool(
    val googleSearch: Map<String, String> = emptyMap()
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null,
    val tools: List<Tool>? = null,
    val generationConfig: GenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?
)

@JsonClass(generateAdapter = true)
data class GLShipInfo(
    val shipName: String,
    val characters: List<String>,
    val seriesTitle: String,
    val status: String,
    val summary: String
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}

suspend fun generateContent(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isEmpty()) return@withContext "API Key is missing."
    val request = GenerateContentRequest(
        contents = listOf(Content(parts = listOf(Part(text = prompt)))),
        systemInstruction = systemInstruction?.let { Content(parts = listOf(Part(text = it))) }
    )
    try {
        val response = GeminiClient.service.generateContent(apiKey, request)
        response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response text"
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}

suspend fun getGLShipsData(query: String): List<GLShipInfo>? = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isEmpty()) return@withContext null
    
    val systemInstructionText = """
        You are an accurate media database extractor specializing in GL (Girls' Love) ships, series, and franchises. 
        Your job is to search the web and return strictly accurate factual data.
        
        You MUST format your entire response as a valid JSON array of objects. Do not include markdown code blocks like ```json. 
        Each object must contain:
        - "shipName": The popular fandom name for the pairing.
        - "characters": An array of the two characters involved.
        - "seriesTitle": The name of the show, book, comic, or media they originate from.
        - "status": Whether the pairing is canon, fanon, or ongoing.
        - "summary": A brief 2-sentence description of their relationship dynamic based on the latest data.
    """.trimIndent()

    val request = GenerateContentRequest(
        contents = listOf(Content(parts = listOf(Part(text = "Fetch accurate, up-to-date information on the following GL ships or series: $query")))),
        systemInstruction = Content(parts = listOf(Part(text = systemInstructionText))),
        tools = listOf(Tool()),
        generationConfig = GenerationConfig(responseMimeType = "application/json")
    )
    
    try {
        val response = GeminiClient.service.generateContent(apiKey, request)
        val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
        if (text != null) {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val type = Types.newParameterizedType(List::class.java, GLShipInfo::class.java)
            val adapter = moshi.adapter<List<GLShipInfo>>(type)
            adapter.fromJson(text)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
