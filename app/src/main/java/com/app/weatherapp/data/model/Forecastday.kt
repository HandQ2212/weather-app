package com.app.weatherapp.data.model

data class Forecastday(
    val astro: Astro,
    val date: String,
    val date_epoch: Double,
    val day: Day,
    val hour: MutableList<Hour>
)