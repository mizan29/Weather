package com.example.weather.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.model.BaseResponse
import com.example.weather.ui.viewmodels.MainActivityViewModel
import com.example.weather.utils.Constants
import com.example.weather.utils.DateUtil
import com.example.weather.utils.SharedPreferencesUtil
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPrefs: SharedPreferencesUtil

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager

    private val PERMISSION_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        //to check location access permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_CODE
                )
            }
        }


        // obtain view-model instance
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        setWeatherData()
        val city = sharedPrefs.getString(SharedPreferencesUtil.CITY_NAME, "")
        if (city.isNotEmpty()) {
            binding.cityName = city
            viewModel.getWeatherDetailsByCity(city)
        } else {
            try {
                val location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                location?.let {
                    viewModel.getWeatherDetails(it.latitude, it.longitude)
                }
            } catch (e: Exception) {

            }
        }

        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onClickSearch(v)
                true
            } else {
                false
            }
        }

    }

    fun onClickSearch(view: View) {
        val text = binding.searchEditText.text.toString().trim()
        sharedPrefs.setString(SharedPreferencesUtil.CITY_NAME, text)
        viewModel.getWeatherDetailsByCity(text)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun setWeatherData() {
        viewModel.weatherInfoResult.observe(this) {
            when (it) {
                is BaseResponse.Loading -> {
                    binding.prgBarWeather.visibility = View.VISIBLE
                    binding.detailsLayout.visibility = View.GONE
                }

                is BaseResponse.Success -> {
                    binding.prgBarWeather.visibility = View.GONE
                    binding.detailsLayout.visibility = View.VISIBLE
                    it.data?.let { data ->
                        val weatherIcon = data.weather?.get(0)?.icon
                        val iconUrl: String =
                            Constants.WEATHER_IMAGE_BASE_URL + weatherIcon + ".png"
                        Picasso.get()
                            .load(iconUrl)
                            .into(binding.weatherImage)

                        val temperature = data.main?.temperature
                        val tempInF: String =
                            temperature.toString() + " " + getString(R.string.unit_f)
                        binding.temperature = tempInF

                        binding.description = data.weather?.get(0)?.description
                        binding.cityTextView.text = data.name + ", " + data.sys.country
                        binding.dateTextView.text = DateUtil.getDate(data.dt)
                        binding.sunriseSunsetTextView.text =
                            getString(R.string.sunrise) + " " + DateUtil.getTime(data.sys.sunrise)
                                ?.plus(", ").plus(
                                    getString(R.string.senset) + " " + DateUtil.getTime(data.sys.sunset)
                                )
                        binding.humidityTextView.text =
                            getString(R.string.humidity) + "\n" + data.main.humidity + "% "
                        binding.cloudsTextView.text =
                            getString(R.string.clouds) + "\n" + data.clouds.all + "% "
                        binding.tempMaxTextView.text =
                            getString(R.string.max_temp) + "\n" + data.main.temperatureMax
                        binding.pressureTextView.text =
                            getString(R.string.pressure) + "\n" + data.main.pressure + " hpa"
                        binding.windsTextView.text =
                            getString(R.string.winds) + "\n" + data.wind.speed + " m/s"
                        binding.tempMinTextView.text =
                            getString(R.string.min_temp) + "\n" + data.main.temperatureMin
                    }
                }

                is BaseResponse.Error -> {
                    binding.prgBarWeather.visibility = View.GONE
                    binding.detailsLayout.visibility = View.VISIBLE
                    Log.e("", "Data from Server: ${it.msg}")
                    Toast.makeText(
                        this,
                        "Weather data loading failed! ${it.msg}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    binding.prgBarWeather.visibility = View.GONE
                    binding.detailsLayout.visibility = View.VISIBLE
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                val location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                location?.let {
                    viewModel.getWeatherDetails(it.latitude, it.longitude)
                }
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Provide Permissions", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}