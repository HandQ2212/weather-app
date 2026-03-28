package com.app.weatherapp.domain.usecase.chat

import com.app.weatherapp.domain.repository.GeminiRepository
import com.app.weatherapp.utils.Resource

class AskGeminiUseCase(
    private val repository: GeminiRepository
) {
    suspend operator fun invoke(
        apiKey: String,
        userMessage: String,
        hiddenWeatherContext: String? = null
    ): Resource<String> {
        return repository.askGemini(apiKey, userMessage, hiddenWeatherContext)
    }
}