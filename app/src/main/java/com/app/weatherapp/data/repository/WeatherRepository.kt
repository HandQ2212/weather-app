package com.app.weatherapp.data.repository

import android.util.Log
import com.app.weatherapp.data.model.SearchCityResponseItem
import com.app.weatherapp.data.remote.WeatherApiService
import com.app.weatherapp.data.model.WeatherResponse
import com.app.weatherapp.utils.Resource

class WeatherRepository(private val apiService: WeatherApiService) {
    companion object {
        private const val TAG = "WeatherRepository"
    }

    suspend fun getWeatherForecast(apiKey: String, location: String): Resource<WeatherResponse> {
        return try {
            val response = apiService.getForecast(apiKey, location)
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "getWeatherForecast success for location=$location")
                Resource.Success(response.body()!!)
            } else {
                val errorMessage = "Forecast failed (${response.code()}): ${response.errorBody()?.string().orEmpty().ifBlank { response.message() }}"
                Log.e(TAG, errorMessage)
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "getWeatherForecast exception for location=$location", e)
            Resource.Error(e.message ?: "Đã xảy ra lỗi kết nối")
        }
    }

    suspend fun searchCity(apiKey: String, query: String): Resource<List<SearchCityResponseItem>> {
        return try {
            val response = apiService.searchCity(apiKey, query)
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "searchCity success for query=$query, count=${response.body()?.size ?: 0}")
                Resource.Success(response.body()!!)
            } else {
                val errorMessage = "Search failed (${response.code()}): ${response.errorBody()?.string().orEmpty().ifBlank { response.message() }}"
                Log.e(TAG, errorMessage)
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "searchCity exception for query=$query", e)
            Resource.Error(e.message ?: "Đã xảy ra lỗi kết nối")
        }
    }
}