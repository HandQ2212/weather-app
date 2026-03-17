package com.app.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast,
    val alerts: Alerts? = null
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val localtime: String
)

data class Current(
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("wind_kph") val windKph: Double,
    val humidity: Int,
    val condition: Condition
)

data class Forecast(
    @SerializedName("forecastday") val forecastDays: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day,
    val hour: List<Hour>
)

data class Day(
    @SerializedName("maxtemp_c") val maxTempC: Double,
    @SerializedName("mintemp_c") val minTempC: Double,
    @SerializedName("avgtemp_c") val avgTempC: Double,
    val condition: Condition
)

data class Hour(
    val time: String,
    @SerializedName("temp_c") val tempC: Double,
    val condition: Condition
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)

data class Alerts(
    val alert: List<AlertItem>
)

data class AlertItem(
    val event: String,
    val desc: String,
    val effective: String
)