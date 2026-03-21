package com.app.weatherapp.data.remote

import com.app.weatherapp.data.model.gemini.GeminiGenerateRequest
import com.app.weatherapp.data.model.gemini.GeminiGenerateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiGenerateRequest
    ): Response<GeminiGenerateResponse>
}
