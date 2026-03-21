package com.app.weatherapp.data.repository

import com.app.weatherapp.data.model.gemini.GeminiCandidate
import com.app.weatherapp.data.model.gemini.GeminiContent
import com.app.weatherapp.data.model.gemini.GeminiGenerateRequest
import com.app.weatherapp.data.model.gemini.GeminiGenerateResponse
import com.app.weatherapp.data.model.gemini.GeminiPart
import com.app.weatherapp.data.remote.GeminiApiService
import com.app.weatherapp.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class GeminiRepositoryTest {

    @Test
    fun askGemini_blankApiKey_returnsError() = runTest {
        val fakeService = FakeGeminiApiService { _, _ ->
            Response.success(
                GeminiGenerateResponse(
                    candidates = listOf(
                        GeminiCandidate(
                            GeminiContent(parts = listOf(GeminiPart("ok")))
                        )
                    )
                )
            )
        }
        val repository = GeminiRepository(fakeService)

        val result = repository.askGemini("", "hello", null)

        assertTrue(result is Resource.Error)
        assertEquals("Thieu GEMINI_API_KEY trong local.properties", result.message)
    }

    @Test
    fun askGemini_success_includesHiddenContextInPromptAndReturnsReply() = runTest {
        val fakeService = FakeGeminiApiService { _, _ ->
            Response.success(
                GeminiGenerateResponse(
                    candidates = listOf(
                        GeminiCandidate(
                            GeminiContent(parts = listOf(GeminiPart("Take a light jacket.")))
                        )
                    )
                )
            )
        }
        val repository = GeminiRepository(fakeService)

        val hiddenContext = "{\"temperature_c\":30,\"condition\":\"Sunny\"}"
        val result = repository.askGemini("api-key", "What should I wear?", hiddenContext)

        assertTrue(result is Resource.Success)
        assertEquals("Take a light jacket.", result.data)

        val sentPrompt = fakeService.lastRequest?.contents?.firstOrNull()?.parts?.firstOrNull()?.text.orEmpty()
        assertTrue(sentPrompt.contains("What should I wear?"))
        assertTrue(sentPrompt.contains(hiddenContext))
        assertTrue(sentPrompt.contains("DU_LIEU_THOI_TIET_AN_KHONG_HIEN_THI_CHO_NGUOI_DUNG"))
    }

    private class FakeGeminiApiService(
        private val responder: suspend (String, GeminiGenerateRequest) -> Response<GeminiGenerateResponse>
    ) : GeminiApiService {

        var lastApiKey: String? = null
        var lastRequest: GeminiGenerateRequest? = null

        override suspend fun generateContent(
            apiKey: String,
            request: GeminiGenerateRequest
        ): Response<GeminiGenerateResponse> {
            lastApiKey = apiKey
            lastRequest = request
            return responder(apiKey, request)
        }
    }
}
