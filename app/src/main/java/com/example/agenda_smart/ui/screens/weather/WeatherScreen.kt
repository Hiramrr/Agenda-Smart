package com.example.agenda_smart.ui.screens.weather
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.agenda_smart.data.model.WeatherResponse

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Fondo azul degradado inspirado en tu boceto
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1976D2), // Azul oscuro
            Color(0xFF4FC3F7)  // Azul claro
        )
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is WeatherUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is WeatherUiState.Success -> {
                val dailyData = state.data.daily

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundBrush)
                ) {
                    // --- SECCIÓN SUPERIOR: Clima de HOY ---
                    // Tomamos el índice 0 que corresponde al día actual
                    val todayWeather = getWeatherInfo(dailyData.weatherCode[0])

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Toma el espacio restante superior
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "XALAPA, VER.",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatDate(dailyData.time[0], 0),
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Ícono gigante del clima
                        Icon(
                            imageVector = todayWeather.icon,
                            contentDescription = todayWeather.description,
                            tint = Color.White,
                            modifier = Modifier.size(120.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Temperatura Principal (Mostramos la máxima como destacada)
                        Text(
                            text = "${dailyData.temperatureMax[0]}°C",
                            color = Color.White,
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = todayWeather.description,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Fila de estadísticas (Lluvia, Min, Max)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            WeatherStatItem(Icons.Filled.WaterDrop, "Lluvia", "${dailyData.precipitationSum[0]} mm")
                            WeatherStatItem(Icons.Filled.ArrowDownward, "Mínima", "${dailyData.temperatureMin[0]}°")
                            WeatherStatItem(Icons.Filled.ArrowUpward, "Máxima", "${dailyData.temperatureMax[0]}°")
                        }
                    }

                    // --- SECCIÓN INFERIOR: Próximos días ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(24.dp)
                    ) {
                        Column {
                            Text(
                                text = "Próximos Días",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Lista horizontal para los días restantes
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Empezamos desde el índice 1 para saltarnos "Hoy"
                                items(dailyData.time.size - 1) { index ->
                                    val realIndex = index + 1
                                    FutureDayCard(
                                        dateStr = dailyData.time[realIndex],
                                        maxTemp = dailyData.temperatureMax[realIndex],
                                        weatherCode = dailyData.weatherCode[realIndex],
                                        dayOffset = realIndex
                                    )
                                }
                            }
                        }
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
                    Button(onClick = { viewModel.getWeather() }, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}

// --- COMPONENTES VISUALES EXTRAS ---

@Composable
fun WeatherStatItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

@Composable
fun FutureDayCard(dateStr: String, maxTemp: Double, weatherCode: Int, dayOffset: Int) {
    val weatherInfo = getWeatherInfo(weatherCode)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp, horizontal = 12.dp)
            .width(70.dp)
    ) {
        Text(
            text = formatDate(dateStr, dayOffset),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Icon(
            imageVector = weatherInfo.icon,
            contentDescription = weatherInfo.description,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "${maxTemp}°",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// --- FUNCIONES DE AYUDA (Mapeo de datos) ---

/**
 * Formatea la fecha de "2026-03-17" a "Hoy", "Mañana", o "Mié 19"
 */
fun formatDate(dateStr: String, offset: Int): String {
    if (offset == 0) return "Hoy"
    if (offset == 1) return "Mañana"

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE d", Locale("es", "MX"))
        val date = inputFormat.parse(dateStr)
        date?.let { outputFormat.format(it).replaceFirstChar { char -> char.uppercase() } } ?: dateStr
    } catch (e: Exception) {
        dateStr
    }
}

/**
 * Clase de datos simple para agrupar el ícono y la descripción
 */
data class WeatherInfo(val icon: ImageVector, val description: String)

/**
 * Convierte el código numérico de Open-Meteo en un ícono y un texto descriptivo
 */
fun getWeatherInfo(code: Int): WeatherInfo {
    return when (code) {
        0 -> WeatherInfo(Icons.Filled.WbSunny, "Despejado")
        1, 2, 3 -> WeatherInfo(Icons.Filled.Cloud, "Nublado")
        45, 48 -> WeatherInfo(Icons.Filled.Dehaze, "Niebla")
        51, 53, 55, 56, 57 -> WeatherInfo(Icons.Filled.WaterDrop, "Llovizna")
        61, 63, 65, 66, 67 -> WeatherInfo(Icons.Filled.WaterDrop, "Lluvia")
        71, 73, 75, 77 -> WeatherInfo(Icons.Filled.AcUnit, "Nieve")
        80, 81, 82 -> WeatherInfo(Icons.Filled.Shower, "Chubascos")
        95, 96, 99 -> WeatherInfo(Icons.Filled.Thunderstorm, "Tormenta")
        else -> WeatherInfo(Icons.Filled.Cloud, "Desconocido")
    }
}