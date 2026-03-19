package com.app.weatherapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.weatherapp.BuildConfig
import com.app.weatherapp.data.local.AppDatabase
import com.app.weatherapp.data.local.UserPreferenceStore
import com.app.weatherapp.data.model.WeatherResponse
import com.app.weatherapp.data.remote.RetrofitInstance
import com.app.weatherapp.data.repository.WeatherRepository
import com.app.weatherapp.databinding.FragmentForecastBinding
import com.app.weatherapp.ui.viewmodel.WeatherViewModel
import com.app.weatherapp.ui.viewmodel.WeatherViewModelFactory
import com.app.weatherapp.utils.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ForecastFragment : Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferenceStore: UserPreferenceStore

    private val weatherViewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(
            WeatherRepository(RetrofitInstance.api),
            AppDatabase.getDatabase(requireContext()).notificationDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceStore = UserPreferenceStore(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            val query = preferenceStore.locationQueryFlow.first().ifBlank { "Hanoi" }
            weatherViewModel.fetchWeather(BuildConfig.WEATHER_API_KEY, query)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.weatherState.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBarForecast.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBarForecast.visibility = View.GONE
                        state.data?.let { renderForecast(it) }
                    }

                    is Resource.Error -> {
                        binding.progressBarForecast.visibility = View.GONE
                        binding.tv24hContent.text = state.message ?: "Không tải được dữ liệu"
                        binding.tv5DayContent.text = ""
                    }
                }
            }
        }
    }

    private fun renderForecast(weather: WeatherResponse) {
        val today = weather.forecast.forecastday.firstOrNull()
        val hourlyText = buildString {
            today?.hour?.take(24)?.forEach { hour ->
                append("${hour.time.takeLast(5)}  |  ${hour.temp_c.toInt()}°  |  ${hour.condition.text}\n")
            }
        }

        val dailyText = buildString {
            weather.forecast.forecastday.take(5).forEach { day ->
                append("${day.date}  |  ${day.day.condition.text}  |  ${day.day.maxtemp_c.toInt()}°\n")
            }
        }

        binding.tv24hContent.text = if (hourlyText.isBlank()) "Không có dữ liệu" else hourlyText
        binding.tv5DayContent.text = if (dailyText.isBlank()) "Không có dữ liệu" else dailyText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}