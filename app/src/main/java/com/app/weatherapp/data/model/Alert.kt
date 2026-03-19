package com.app.weatherapp.data.model

data class Alert(
    val areas: Any,
    val category: String,
    val certainty: Any,
    val desc: String,
    val effective: String,
    val event: String,
    val expires: String,
    val headline: String,
    val instruction: String,
    val msgtype: Any,
    val note: Any,
    val severity: Any,
    val urgency: Any
)