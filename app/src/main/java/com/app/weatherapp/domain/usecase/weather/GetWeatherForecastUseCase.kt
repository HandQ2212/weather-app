package com.app.weatherapp.domain.usecase.weather

import com.app.weatherapp.data.model.WeatherResponse
import com.app.weatherapp.domain.repository.WeatherRepository
import com.app.weatherapp.utils.Resource

class GetWeatherForecastUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(apiKey: String, location: String): Resource<WeatherResponse> {
        return repository.getWeatherForecast(apiKey, location)
    }
}
