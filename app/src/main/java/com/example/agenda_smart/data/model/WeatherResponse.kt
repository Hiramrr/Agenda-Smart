package com.example.agenda_smart.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("daily")
    val daily: DailyWeather
)


data class DailyWeather(
    @SerializedName("time")
    val time: List<String>,

    @SerializedName("temperature_2m_max")
    val temperatureMax: List<Double>,

    @SerializedName("temperature_2m_min")
    val temperatureMin: List<Double>,

    @SerializedName("precipitation_sum")
    val precipitationSum: List<Double>,

    @SerializedName("weathercode")
    val weatherCode: List<Int>
)