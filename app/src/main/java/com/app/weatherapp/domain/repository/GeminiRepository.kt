package com.app.weatherapp.domain.repository

import com.app.weatherapp.utils.Resource

interface GeminiRepository {
    suspend fun askGemini(
        apiKey: String,
        userMessage: String,
        hiddenWeatherContext: String? = null
    ): Resource<String>
}
