package com.app.weatherapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.weatherapp.BuildConfig
import com.app.weatherapp.data.local.UserPreferenceStore
import com.app.weatherapp.data.remote.RetrofitInstance
import com.app.weatherapp.data.repository.GeminiRepository
import com.app.weatherapp.data.repository.WeatherRepository
import com.app.weatherapp.databinding.FragmentChatBinding
import com.app.weatherapp.ui.adapter.ChatMessageAdapter
import com.app.weatherapp.ui.viewmodel.ChatViewModel
import com.app.weatherapp.ui.viewmodel.ChatViewModelFactory
import com.app.weatherapp.utils.Resource
import kotlin.math.max
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceStore: UserPreferenceStore
    private val weatherRepository by lazy { WeatherRepository(RetrofitInstance.apiService) }
    private var hiddenWeatherContext: String? = null

    private val chatAdapter by lazy { ChatMessageAdapter() }

    private val chatViewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(
            GeminiRepository(RetrofitInstance.geminiApiService)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceStore = UserPreferenceStore(requireContext())
        setupUi()
        observeViewModel()
        loadHiddenWeatherContext()
    }

    private fun setupUi() {
        applyKeyboardInsets()

        binding.recyclerMessages.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSend.setOnClickListener {
            submitMessage()
        }

        binding.etMessage.setOnEditorActionListener { _, _, _ ->
            submitMessage()
            true
        }
    }

    private fun submitMessage() {
        val message = binding.etMessage.text?.toString().orEmpty()
        chatViewModel.sendMessage(BuildConfig.GEMINI_API_KEY, message, hiddenWeatherContext)
        binding.etMessage.text?.clear()
    }

    private fun loadHiddenWeatherContext() {
        viewLifecycleOwner.lifecycleScope.launch {
            val currentLocation = preferenceStore.locationQueryFlow.first().ifBlank { "Hanoi" }
            when (val weatherResult = weatherRepository.getWeatherForecast(BuildConfig.WEATHER_API_KEY, currentLocation)) {
                is Resource.Success -> {
                    val weather = weatherResult.data ?: return@launch
                    hiddenWeatherContext = """
                        {
                          "location": "${weather.location.name}",
                          "localtime": "${weather.location.localtime}",
                          "temperature_c": ${weather.current.temp_c},
                          "feelslike_c": ${weather.current.feelslike_c},
                          "humidity": ${weather.current.humidity},
                          "wind_kph": ${weather.current.wind_kph},
                          "condition": "${weather.current.condition.text}"
                        }
                    """.trimIndent()
                }
                is Resource.Error -> {
                    hiddenWeatherContext = null
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun applyKeyboardInsets() {
        val initialBottom = binding.root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val systemBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            val extraBottom = max(imeBottom, systemBottom)
            view.updatePadding(bottom = initialBottom + extraBottom)
            insets
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.messages.collectLatest { messages ->
                chatAdapter.submitList(messages) {
                    if (messages.isNotEmpty()) {
                        binding.recyclerMessages.scrollToPosition(messages.lastIndex)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.isLoading.collectLatest { loading ->
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
