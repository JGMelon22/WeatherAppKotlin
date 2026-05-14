package com.example.weatherappkotlin

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.example.weatherappkotlin.databinding.ActivityMainBinding
import com.example.weatherappkotlin.model.UiState
import com.example.weatherappkotlin.network.RetrofitClient
import com.example.weatherappkotlin.repository.WeatherRepository
import com.example.weatherappkotlin.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;

    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(WeatherRepository(RetrofitClient.weatherApi))
    }

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
            viewModel.search(binding.cityInput.text.toString())
        }

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: UiState) {
        // Show loading, hide everything else
        binding.loadingSpinner.visibility = View.GONE
        binding.weatherCard.visibility = View.GONE
        binding.errorMessage.visibility = View.GONE

        when (state) {
            UiState.Idle -> {
                // Nothing to show. Fresh app launch
            }

            UiState.Loading -> {
                binding.loadingSpinner.visibility = View.VISIBLE
            }

            is UiState.Success -> {
                binding.weatherCard.visibility = View.VISIBLE
                val data = state.data
                binding.cityName.text = data.cityAndCountry
                binding.temperature.text = "${data.temperatureC}°C"
                binding.description.text = data.description
                binding.feelsLike.text = "Feels like ${data.feelsLikeC}°C"
                binding.humidity.text = "${data.humidityPercent}%"
                binding.wind.text = "${data.windMs}m/s"
                binding.pressure.text = "${data.pressureHpa}hPa"
                binding.weatherIcon.load(data.iconUrl)
            }

            is UiState.Error -> {
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = state.message
            }
        }
    }

    private class WeatherViewModelFactory(
        private val repository: WeatherRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository) as T
        }
    }
}