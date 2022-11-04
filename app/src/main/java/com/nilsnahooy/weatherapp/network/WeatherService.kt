package com.nilsnahooy.weatherapp.network

import com.nilsnahooy.weatherapp.models.WeatherDataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("2.5/weather")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String?,
        @Query("appid") appId: String?
    ): Call<WeatherDataResponse>
}