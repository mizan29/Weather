package com.example.weather.repository

import com.example.weather.api.ApiService
import com.example.weather.utils.Constants
import javax.inject.Inject

class ApiRepository@Inject constructor(private val apiService: ApiService) {
    suspend fun getWeatherDetails(lat: Double, lon: Double) = apiService.getWeatherDetails(lat, lon, "imperial",Constants.API_KEY)
    suspend fun getWeatherDetailsByCity(city: String) = apiService.getWeatherDetailsByCity(city, "imperial", Constants.API_KEY)
}