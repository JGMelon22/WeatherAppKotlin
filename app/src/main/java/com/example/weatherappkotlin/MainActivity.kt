package com.example.weatherappkotlin

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.weatherappkotlin.databinding.ActivityMainBinding
import com.example.weatherappkotlin.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.searchButton.setOnClickListener {
            val city = binding.cityInput.text.toString().trim()
            if (city.isNotEmpty())
                fetchWeather(city)
        }
    }

    private fun fetchWeather(city: String) {
        // Show loading, hide everything else
        binding.loadingSpinner.visibility = View.VISIBLE
        binding.weatherCard.visibility = View.GONE
        binding.errorMessage.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.weatherApi.getWeather(
                    city = city,
                    apiKey = BuildConfig.OPEN_WEATHER_API_KEY
                )

                // Success - populate UI
                binding.cityName.text = "${response.name}, ${response.sys.country}"
                binding.temperature.text = "${response.main.temp.toInt()}°C"
                binding.description.text =
                    response.weather.first().description.replaceFirstChar() { it.uppercase() }
                binding.feelsLike.text = "Feels like ${response.main.feelsLike.toInt()}°C"
                binding.humidity.text = "${response.main.humidity}%"
                binding.wind.text = "${response.wind.speed}m/s"
                binding.pressure.text = "${response.main.pressure}hPa"

                val iconUrl =
                    "https://openweathermap.org/img/wn/${response.weather.first().icon}@2x.png"

                binding.weatherIcon.load(iconUrl)

                binding.loadingSpinner.visibility = View.GONE
                binding.weatherCard.visibility = View.VISIBLE
            } catch (e: retrofit2.HttpException) {
                binding.loadingSpinner.visibility = View.GONE
                binding.errorMessage.text = when (e.code()) {
                    401 -> "Invalid API key"
                    404 -> "City not found"
                    else -> "HTTP error ${e.code()}"
                }
                binding.errorMessage.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding.loadingSpinner.visibility = View.GONE
                binding.errorMessage.text = "Network error: ${e.message}"
                binding.errorMessage.visibility = View.VISIBLE
            }
        }
    }
}