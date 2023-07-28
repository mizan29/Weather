package com.example.weather.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Main(
    @SerializedName("temp") val temperature: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val temperatureMin: Double,
    @SerializedName("temp_max") val temperatureMax: Double,
    @SerializedName("pressure") val pressure: Double,
    @SerializedName("humidity") val humidity: Double,
    @SerializedName("sea_level") val seaLevel: Double,
    @SerializedName("grnd_level") val groundLevel: Double,
): Serializable