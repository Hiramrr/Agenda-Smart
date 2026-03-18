package com.example.agenda_smart.ui.screens.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenda_smart.data.model.WeatherResponse
import com.example.agenda_smart.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//posibles estados de la pantalla
sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(val data: WeatherResponse) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

class WeatherViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        getWeather() // Cargamos el clima al iniciar el ViewModel
    }

    fun getWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                // Llammaos a la appi con RetrofitInstance
                val response = RetrofitInstance.api.getDailyWeather()
                _uiState.value = WeatherUiState.Success(response) // Éxito
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("No se pudo cargar el clima. Verifica tu conexión.")
            }
        }
    }
}