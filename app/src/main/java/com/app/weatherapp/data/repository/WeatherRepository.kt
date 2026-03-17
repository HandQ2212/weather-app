package com.app.weatherapp.data.repository

import com.app.weatherapp.data.model.SearchCityResponseItem
import com.app.weatherapp.data.remote.WeatherApiService
import com.app.weatherapp.data.model.WeatherResponse
import com.app.weatherapp.utils.Resource

class WeatherRepository(private val apiService: WeatherApiService) {

    suspend fun getWeatherForecast(apiKey: String, location: String): Resource<WeatherResponse> {
        return try {
            val response = apiService.getForecast(apiKey, location)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Đã xảy ra lỗi kết nối")
        }
    }

    suspend fun searchCity(apiKey: String, query: String): Resource<List<SearchCityResponseItem>> {
        return try {
            val response = apiService.searchCity(apiKey, query)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Đã xảy ra lỗi kết nối")
        }
    }
}