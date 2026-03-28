package com.app.weatherapp.domain.usecase.weather

import com.app.weatherapp.data.model.SearchCityResponseItem
import com.app.weatherapp.domain.repository.WeatherRepository
import com.app.weatherapp.utils.Resource

class SearchCityUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(apiKey: String, query: String): Resource<List<SearchCityResponseItem>> {
        return repository.searchCity(apiKey, query)
    }
}
