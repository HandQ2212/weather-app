package com.app.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.weatherapp.data.local.NotificationDao
import com.app.weatherapp.data.model.NotificationEntity
import com.app.weatherapp.data.model.SearchCityResponseItem
import com.app.weatherapp.data.model.WeatherResponse
import com.app.weatherapp.data.repository.WeatherRepository
import com.app.weatherapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val notificationDao: NotificationDao
) : ViewModel(){
    private val _weatherState = MutableStateFlow<Resource<WeatherResponse>>(Resource.Loading())
    val weatherState: StateFlow<Resource<WeatherResponse>> = _weatherState

    private val _searchState = MutableStateFlow<Resource<List<SearchCityResponseItem>>>(Resource.Success(emptyList()))
    val searchState: StateFlow<Resource<List<SearchCityResponseItem>>> = _searchState

    fun fetchWeather(apiKey: String, location: String) {
        viewModelScope.launch{
            _weatherState.value = Resource.Loading()
            val result = repository.getWeatherForecast(apiKey, location)

            if (result is Resource.Success) {
                val alertsList = result.data?.alerts?.alert

                if (!alertsList.isNullOrEmpty()) {
                    alertsList.forEach { alertItem ->
                        val newNotification = NotificationEntity(
                            title = alertItem.event,
                            description = alertItem.desc,
                            timeAgo = "Vừa xong",
                            iconType = "warning"
                        )
                        notificationDao.insertNotification(notification = newNotification)
                    }
                }
            }

            _weatherState.value = result
        }
    }

    fun searchCity(apiKey: String, query: String) {
        if (query.isBlank()) {
            _searchState.value = Resource.Success(emptyList())
            return
        }

        viewModelScope.launch {
            _searchState.value = Resource.Loading()
            val result = repository.searchCity(apiKey, query)
            _searchState.value = result
        }
    }
}