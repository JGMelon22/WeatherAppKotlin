package com.example.weatherappkotlin.model

sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data class Success(val data: WeatherUiData) : UiState()
    data class Error(val data: String) : UiState()
}

data class WeatherUiData(
    val cityAndCountry: String,
    val temperatureC: Int,
    val description: String,
    val feelsLikeC: Int,
    val humidityPercent: Int,
    val windMs: Double,
    val pressureHpa: Int,
    val iconUrl: String
)