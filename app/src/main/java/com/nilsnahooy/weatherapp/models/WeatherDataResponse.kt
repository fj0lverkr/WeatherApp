package com.nilsnahooy.weatherapp.models

import java.io.Serializable

data class WeatherDataResponse(
    val coord: Coordinates,
    val weather: List<WeatherDetail>,
    val base: String,
    val main: MainData,
    val visibility: Int,
    val wind: WindData,
    val rain: RainData,
    val clouds: CloudData,
    val dt: Int,
    val sys:SysData,
    val timeZone: Int,
    val id: Int,
    val name: String,
    val cod: Int
): Serializable

data class Coordinates(
    val lon: Double,
    val lat: Double
): Serializable

data class WeatherDetail(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
): Serializable

data class MainData(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int,
    val grnd_level: Int
): Serializable

data class WindData(
    val speed: Double,
    val deg: Int,
    val gust: Double
): Serializable

data class RainData(
    val h: Double
): Serializable

data class CloudData(
    val all: Int
): Serializable

data class SysData(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Int,
    val sunset: Int
): Serializable
