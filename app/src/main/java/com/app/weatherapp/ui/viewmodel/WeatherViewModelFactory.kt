package com.app.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.weatherapp.data.local.NotificationDao
import com.app.weatherapp.data.repository.WeatherRepository

class WeatherViewModelFactory(
    private val repository: WeatherRepository,
    private val notificationDao: NotificationDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository, notificationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}