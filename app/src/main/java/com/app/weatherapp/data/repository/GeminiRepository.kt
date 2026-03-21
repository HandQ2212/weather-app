package com.app.weatherapp.data.repository

import android.util.Log
import com.app.weatherapp.data.model.gemini.GeminiContent
import com.app.weatherapp.data.model.gemini.GeminiGenerateRequest
import com.app.weatherapp.data.model.gemini.GeminiPart
import com.app.weatherapp.data.remote.GeminiApiService
import com.app.weatherapp.utils.Resource

class GeminiRepository(private val apiService: GeminiApiService) {
    companion object {
        private const val TAG = "GeminiRepository"

        private const val SYSTEM_CONTEXT =
            "Ban la tro ly thoi tiet trong ung dung Weather App. " +
                "Hay tra loi ngan gon, ro rang, uu tien goi y hanh dong thuc te theo tinh hinh thoi tiet."
    }

    suspend fun askGemini(
        apiKey: String,
        userMessage: String,
        hiddenWeatherContext: String? = null
    ): Resource<String> {
        if (apiKey.isBlank()) {
            return Resource.Error("Thieu GEMINI_API_KEY trong local.properties")
        }

        return try {
            val weatherContextPrompt = if (hiddenWeatherContext.isNullOrBlank()) {
                ""
            } else {
                "\n\n[DU_LIEU_THOI_TIET_AN_KHONG_HIEN_THI_CHO_NGUOI_DUNG]\n$hiddenWeatherContext"
            }

            val prompt = "$SYSTEM_CONTEXT$weatherContextPrompt\n\nNguoi dung hoi: $userMessage"
            val request = GeminiGenerateRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(text = prompt)
                        )
                    )
                )
            )

            val response = apiService.generateContent(apiKey, request)
            if (!response.isSuccessful) {
                val message = "Gemini failed (${response.code()}): ${response.errorBody()?.string().orEmpty().ifBlank { response.message() }}"
                Log.e(TAG, message)
                return Resource.Error(message)
            }

            val reply = response.body()
                ?.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?.trim()

            if (reply.isNullOrBlank()) {
                Resource.Error("Khong nhan duoc noi dung tra loi tu Gemini")
            } else {
                Resource.Success(reply)
            }
        } catch (e: Exception) {
            Log.e(TAG, "askGemini exception", e)
            Resource.Error(e.message ?: "Da xay ra loi ket noi")
        }
    }
}
