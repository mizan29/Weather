package com.example.weather.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Wind(
    @SerializedName("speed") val speed: Double
): Serializable
