package com.example.weather.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.BaseResponse
import com.example.weather.model.WeatherResponse
import com.example.weather.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val repository: ApiRepository): ViewModel() {

    private val _weatherInfoResult: MutableLiveData<BaseResponse<WeatherResponse>> = MutableLiveData()
    val weatherInfoResult: LiveData<BaseResponse<WeatherResponse>> = _weatherInfoResult

    fun getWeatherDetails(lat: Double, lon: Double){
        _weatherInfoResult.value = BaseResponse.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getWeatherDetails(lat, lon)
                if (response.code() == 200) {
                    _weatherInfoResult.postValue(BaseResponse.Success(response.body()))
                } else {
                    _weatherInfoResult.postValue(BaseResponse.Error(response.message()))
                }
            }catch (ex: Exception) {
                _weatherInfoResult.postValue(BaseResponse.Error(ex.message))
            }
        }
    }

    fun getWeatherDetailsByCity(city: String){
        _weatherInfoResult.value = BaseResponse.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getWeatherDetailsByCity(city)
                if (response.code() == 200) {
                    _weatherInfoResult.postValue(BaseResponse.Success(response.body()))
                } else {
                    _weatherInfoResult.postValue(BaseResponse.Error(response.message()))
                }
            }catch (ex: Exception) {
                _weatherInfoResult.postValue(BaseResponse.Error(ex.message))
            }
        }
    }
}