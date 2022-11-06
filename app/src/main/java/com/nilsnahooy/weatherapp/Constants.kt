package com.nilsnahooy.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

object Constants {
    val REQUIRED_PERMISSIONS_LOCATION =
        mutableListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).toTypedArray()

    const val REQUEST_CODE_LOCATION = 111
    const val TAG = "DEV"
    const val BASE_URL = "https://api.openweathermap.org/data/"
    const val METRIC_UNIT = "metric"
    const val IMPERIAL_UNIT = "imperial"

    fun isNetworkEnabled(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when{
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }

        } else {
            @Suppress("DEPRECATION")
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }
    }

    fun setupLocationProvider(context: Context): FusedLocationProviderClient {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if(!gpsEnabled && !networkEnabled) {
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        return LocationServices.getFusedLocationProviderClient(context)
    }

    fun getWeatherCondition(weatherCode: Int): Int {
        return when(weatherCode){
            in(200..232),
            in(701..781) -> R.drawable.storm
            in(300..321),
            in(500..531) -> R.drawable.rain
            in(600..622) -> R.drawable.snowflake
            in(801..804) -> R.drawable.cloud
            800 -> R.drawable.sunny
            else -> -1 //this should not be possible
        }
    }

    fun getWindCardinal(degrees: Int): String {
        Log.i(TAG, "getWindCardinal: $degrees")
        return when(degrees.toDouble()){
            in(348.75..11.25) -> "N"
            in(11.25..33.75) -> "NNE"
            in(33.75..56.25) -> "NE"
            in(56.25..78.75) -> "ENE"
            in(78.75..101.25) -> "E"
            in(101.25..123.75) -> "ESE"
            in(123.75..146.25) -> "SE"
            in(146.25..168.75) -> "SSE"
            in(168.75..191.25) -> "S"
            in(191.25..213.75) -> "SSW"
            in(213.75..236.25) -> "SW"
            in(236.25..258.75) -> "WSW"
            in(258.75..281.25) -> "W"
            in(281.25..303.75) -> "WNW"
            in(303.75..326.25) -> "NW"
            in(326.25..348.75) -> "NNW"
            else -> "N"
        }
    }
}