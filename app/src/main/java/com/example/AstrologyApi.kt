package com.example

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@JsonClass(generateAdapter = true)
data class HoroscopeResponse(
    val data: HoroscopeData?
)

@JsonClass(generateAdapter = true)
data class HoroscopeData(
    val date: String?,
    @field:Json(name = "horoscope_data") val horoscopeData: String?
)

interface AstrologyApiService {
    @GET("api/v1/get-horoscope/daily")
    suspend fun getDailyHoroscope(
        @Query("sign") sign: String,
        @Query("day") day: String = "today"
    ): HoroscopeResponse
}

object AstrologyClient {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    
    val service: AstrologyApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://freehoroscopeapi.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AstrologyApiService::class.java)
    }
}
