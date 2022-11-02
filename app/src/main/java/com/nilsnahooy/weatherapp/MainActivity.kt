package com.nilsnahooy.weatherapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.Manifest
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
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
    private lateinit var locationManager: FusedLocationProviderClient
    private var latitude = 0.0
    private var longitude = 0.0

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult.locations.isNotEmpty()) {
                val location = locationResult.lastLocation!!
                latitude = location.latitude
                longitude = location.longitude
                b?.tvTemp?.text = "lat: $latitude long: $longitude"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b?.root)

        locationManager = checkLocationProviderAvailable()

        //setup actionbar
        setSupportActionBar(b?.tbMainToolbar)

       getCurrentLocation()
    }

    //add the menu to the actionbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_refresh_location -> {
                getCurrentLocation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkLocationProviderAvailable(): FusedLocationProviderClient {
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
        return LocationServices.getFusedLocationProviderClient(this)
    }

    private fun locationPermissionsGranted() = REQUIRED_PERMISSIONS_LOCATION.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentLocation() {
        if (locationPermissionsGranted()) {
            val locationRequest = LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
                .setMaxUpdates(1)
                .build()
            try {
                locationManager.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS_LOCATION, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        b = null
    }
}