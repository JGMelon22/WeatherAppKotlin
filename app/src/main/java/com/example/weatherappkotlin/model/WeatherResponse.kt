package com.example.weatherappkotlin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val name: String,
    val weather: List<Weather>,
    val main: MainInfo,
    val wind: Wind,
    val sys: Sys
)

@Serializable
data class Weather(
    val description: String,
    val icon: String
)

@Serializable
data class MainInfo(
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    val humidity: Int,
    val pressure: Int
)

@Serializable
data class Wind(
    val speed: Double
)

@Serializable
data class Sys(
    val country: String
)