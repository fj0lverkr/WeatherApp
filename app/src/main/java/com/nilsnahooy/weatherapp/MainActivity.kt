package com.nilsnahooy.weatherapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nilsnahooy.weatherapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    companion object {
        private val REQUIRED_PERMISSIONS_LOCATION =
            mutableListOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).toTypedArray()
        private const val REQUEST_CODE_PERMISSIONS = 111
    }

    private var b: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b?.root)

        checkLocationProviderAvailable()

        if(locationPermissionsGranted()){
            //Do things
        } else {
            ActivityCompat.requestPermissions(this,
                REQUIRED_PERMISSIONS_LOCATION, REQUEST_CODE_PERMISSIONS)
        }

    }

    private fun checkLocationProviderAvailable() {
        val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun locationPermissionsGranted() = REQUIRED_PERMISSIONS_LOCATION.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        b = null
    }
}