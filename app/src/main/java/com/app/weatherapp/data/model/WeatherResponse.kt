package com.app.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("alerts") val alerts: Alerts?,
    @SerializedName("current") val current: Current,
    @SerializedName("forecast") val forecast: Forecast,
    @SerializedName("location") val location: Location
)