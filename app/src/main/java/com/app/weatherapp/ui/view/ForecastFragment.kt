package com.app.weatherapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.weatherapp.BuildConfig
import com.app.weatherapp.data.local.AppDatabase
import com.app.weatherapp.data.local.UserPreferenceStore
import com.app.weatherapp.data.model.Forecastday
import com.app.weatherapp.data.model.WeatherResponse
import com.app.weatherapp.data.remote.RetrofitInstance
import com.app.weatherapp.data.repository.WeatherRepository
import com.app.weatherapp.databinding.FragmentForecastBinding
import com.app.weatherapp.ui.adapter.DailyForecastAdapter
import com.app.weatherapp.ui.adapter.HourlyForecastAdapter
import com.app.weatherapp.ui.viewmodel.WeatherViewModel
import com.app.weatherapp.ui.viewmodel.WeatherViewModelFactory
import com.app.weatherapp.utils.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ForecastFragment : Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferenceStore: UserPreferenceStore
    private val hourlyAdapter = HourlyForecastAdapter()
    private val dailyAdapter = DailyForecastAdapter()

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
        setupRecyclerViews()

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
                        binding.tvHourlyEmpty.visibility = View.VISIBLE
                        binding.tvDailyEmpty.visibility = View.VISIBLE
                        binding.tvHourlyEmpty.text = state.message ?: "Không tải được dữ liệu"
                        hourlyAdapter.submitList(emptyList())
                        dailyAdapter.submitList(emptyList())
                    }
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        binding.rvHourlyForecast.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvHourlyForecast.adapter = hourlyAdapter

        binding.rvDailyForecast.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDailyForecast.adapter = dailyAdapter
    }

    private fun renderForecast(weather: WeatherResponse) {
        val hourlyData = weather.forecast.forecastday
            .firstOrNull()
            ?.hour
            ?.take(24)
            ?.map { hour ->
                HourlyForecastAdapter.HourlyUiModel(
                    time = formatHour(hour.time),
                    temperature = "${hour.temp_c.toInt()}°",
                    condition = hour.condition.text,
                    rainChance = "Mưa: ${hour.chance_of_rain.toInt()}%",
                    wind = "Gió: ${hour.wind_kph.toInt()} km/h"
                )
            }
            .orEmpty()

        val dailyData = weather.forecast.forecastday
            .take(5)
            .map { day ->
                DailyForecastAdapter.DailyUiModel(
                    date = formatDate(day),
                    condition = day.day.condition.text,
                    tempRange = "${day.day.maxtemp_c.toInt()}° / ${day.day.mDoubleemp_c.toInt()}°",
                    extraInfo = "Mưa ${day.day.daily_chance_of_rain.toInt()}% • Ẩm ${day.day.avghumidity.toInt()}%"
                )
            }

        binding.tvHourlyEmpty.visibility = if (hourlyData.isEmpty()) View.VISIBLE else View.GONE
        binding.tvDailyEmpty.visibility = if (dailyData.isEmpty()) View.VISIBLE else View.GONE

        hourlyAdapter.submitList(hourlyData)
        dailyAdapter.submitList(dailyData)
    }

    private fun formatHour(rawTime: String): String {
        return rawTime.takeLast(5)
    }

    private fun formatDate(day: Forecastday): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val output = SimpleDateFormat("EEE, dd/MM", Locale.forLanguageTag("vi-VN"))
            val parsed = input.parse(day.date)
            output.format(parsed ?: return day.date)
        } catch (_: Exception) {
            day.date
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}