package com.example.weatherappkotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherappkotlin.model.UiState
import com.example.weatherappkotlin.model.WeatherResponse
import com.example.weatherappkotlin.model.WeatherUiData
import com.example.weatherappkotlin.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow();

    fun search(city: String) {
        val trimmed = city.trim()
        if (trimmed.isEmpty()) return

        _uiState.value = UiState.Loading

        viewModelScope.launch {
            _uiState.value = try {
                val response = repository.getWeather(trimmed)
                UiState.Success(response.toUiData())
            } catch (e: HttpException) {
                UiState.Error(
                    when (e.code()) {
                        401 -> "Invalid API key"
                        404 -> "City not found"
                        else -> "HTTP`error ${e.code()}"
                    }
                )
            } catch (e: IOException) {
                UiState.Error("Network error. Check your connection.")
            } catch (e: Exception) {
                UiState.Error("Unexpected error: ${e.message}")
            }
        }
    }
}

private fun WeatherResponse.toUiData(): WeatherUiData {
    val first = weather.first()
    return WeatherUiData(
        cityAndCountry = "$name, ${sys.country}",
        temperatureC = main.temp.toInt(),
        description = first.description.replaceFirstChar { it.uppercase() },
        feelsLikeC = main.feelsLike.toInt(),
        humidityPercent = main.humidity,
        windMs = wind.speed,
        pressureHpa = main.pressure,
        iconUrl = "https://openweathermap.org/img/wn/${first.icon}@2x.png"
    )
}