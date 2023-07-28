package com.example.weather.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Coord(
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lon") val longitude: Double
): Serializable
