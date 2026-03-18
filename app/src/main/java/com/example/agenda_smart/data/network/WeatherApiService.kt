package com.example.agenda_smart.data.network

import com.example.agenda_smart.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun getDailyWeather(
        //Valores para xalapa
        @Query("latitude") latitude: Double = 19.5438,
        @Query("longitude") longitude: Double = -96.9102,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_sum,weathercode",
        @Query("timezone") timezone: String = "America/Mexico_City",
        @Query("forecast_days") forecastDays: Int = 7
    ): WeatherResponse
}