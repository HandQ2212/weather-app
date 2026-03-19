package com.app.weatherapp.data.remote

import com.app.weatherapp.data.model.SearchCityResponseItem
import com.app.weatherapp.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 5
    ): Response<WeatherResponse>

    @GET("v1/search.json")
    suspend fun searchCity(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): Response<List<SearchCityResponseItem>>
}