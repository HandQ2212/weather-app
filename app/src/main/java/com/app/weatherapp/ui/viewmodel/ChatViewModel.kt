package com.app.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.weatherapp.data.repository.GeminiRepository
import com.app.weatherapp.ui.model.ChatMessageUi
import com.app.weatherapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: GeminiRepository
) : ViewModel() {

    private val _messages = MutableStateFlow(
        listOf(
            ChatMessageUi(
                id = System.currentTimeMillis(),
                text = "Xin chào! Tôi là trợ lý ảo của bạn. Bạn có thể hỏi tôi những vấn đề về thời tiết như nên mặc gì vào thời tiết hôm nay, có nên đem áo mưa hoặc lập kế hoạch di chuyển theo thời tiết",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessageUi>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMessage(apiKey: String, userText: String, hiddenWeatherContext: String?) {
        val cleanedText = userText.trim()
        if (cleanedText.isBlank() || _isLoading.value) return

        val userMessage = ChatMessageUi(
            id = System.currentTimeMillis(),
            text = cleanedText,
            isUser = true
        )

        _messages.update { it + userMessage }

        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.askGemini(apiKey, cleanedText, hiddenWeatherContext)) {
                is Resource.Success -> {
                    val botMessage = ChatMessageUi(
                        id = System.currentTimeMillis() + 1,
                        text = result.data.orEmpty(),
                        isUser = false
                    )
                    _messages.update { it + botMessage }
                }
                is Resource.Error -> {
                    val botMessage = ChatMessageUi(
                        id = System.currentTimeMillis() + 1,
                        text = result.message ?: "Da xay ra loi, vui long thu lai",
                        isUser = false
                    )
                    _messages.update { it + botMessage }
                }
                is Resource.Loading -> Unit
            }
            _isLoading.value = false
        }
    }
}
