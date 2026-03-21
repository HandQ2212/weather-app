package com.app.weatherapp.ui.viewmodel

import com.app.weatherapp.MainDispatcherRule
import com.app.weatherapp.data.repository.GeminiRepository
import com.app.weatherapp.utils.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: GeminiRepository = mockk()

    @Test
    fun sendMessage_blankMessage_doesNotCallRepository() = runTest {
        val viewModel = ChatViewModel(repository)

        val beforeCount = viewModel.messages.value.size
        viewModel.sendMessage("key", "   ", "weather-context")
        advanceUntilIdle()

        assertEquals(beforeCount, viewModel.messages.value.size)
        coVerify(exactly = 0) { repository.askGemini(any(), any(), any()) }
    }

    @Test
    fun sendMessage_success_appendsUserAndBotMessages() = runTest {
        coEvery { repository.askGemini("key", "Should I bring an umbrella?", "ctx") } returns Resource.Success("Yes, bring one.")

        val viewModel = ChatViewModel(repository)
        viewModel.sendMessage("key", "Should I bring an umbrella?", "ctx")
        advanceUntilIdle()

        val messages = viewModel.messages.value
        assertTrue(messages.size >= 3)
        assertEquals("Should I bring an umbrella?", messages[messages.lastIndex - 1].text)
        assertTrue(messages[messages.lastIndex - 1].isUser)
        assertEquals("Yes, bring one.", messages.last().text)
        assertFalse(messages.last().isUser)

        coVerify(exactly = 1) { repository.askGemini("key", "Should I bring an umbrella?", "ctx") }
    }

    @Test
    fun sendMessage_error_appendsErrorMessage() = runTest {
        coEvery { repository.askGemini("key", "weather", any()) } returns Resource.Error("network error")

        val viewModel = ChatViewModel(repository)
        viewModel.sendMessage("key", "weather", null)
        advanceUntilIdle()

        val last = viewModel.messages.value.last()
        assertEquals("network error", last.text)
        assertFalse(last.isUser)
    }
}
