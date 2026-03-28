package com.app.weatherapp.domain.repository

import com.app.weatherapp.data.model.SearchCityResponseItem
import com.app.weatherapp.data.model.WeatherResponse
import com.app.weatherapp.utils.Resource

interface WeatherRepository {
    suspend fun getWeatherForecast(apiKey: String, location: String): Resource<WeatherResponse>

    suspend fun searchCity(apiKey: String, query: String): Resource<List<SearchCityResponseItem>>
}
