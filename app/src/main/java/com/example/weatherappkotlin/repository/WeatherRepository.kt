package com.example.weatherappkotlin.repository

import com.example.weatherappkotlin.BuildConfig
import com.example.weatherappkotlin.model.WeatherResponse
import com.example.weatherappkotlin.network.WeatherApi

class WeatherRepository(
    private val api: WeatherApi,
    private val apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY
) {
    suspend fun getWeather(city: String): WeatherResponse {
        return api.getWeather(city = city, apiKey = apiKey)
    }
}