package com.example.agenda_smart.ui.screens.weather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel()
) {
    // Obtenemos el estado del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (val state = uiState) {
            is WeatherUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator() //Indicamos que esta cargando
                }
            }
            is WeatherUiState.Success -> {
                val dailyData = state.data.daily
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Pronóstico para Xalapa",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(dailyData.time.size) { index ->
                        WeatherDayCard(
                            date = dailyData.time[index],
                            maxTemp = dailyData.temperatureMax[index],
                            minTemp = dailyData.temperatureMin[index],
                            precip = dailyData.precipitationSum[index]
                        )
                    }
                }
            }
            is WeatherUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.getWeather() }) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherDayCard(date: String, maxTemp: Double, minTemp: Double, precip: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Fecha: $date", fontWeight = FontWeight.Bold)
                Text(text = "Lluvia: $precip mm", style = MaterialTheme.typography.bodyMedium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Máx: $maxTemp °C", color = MaterialTheme.colorScheme.error)
                Text(text = "Mín: $minTemp °C", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}