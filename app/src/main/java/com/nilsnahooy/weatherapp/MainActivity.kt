package com.nilsnahooy.weatherapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.Manifest
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.nilsnahooy.weatherapp.Constants.isNetworkEnabled
import com.nilsnahooy.weatherapp.Constants.setupLocationProvider
import com.nilsnahooy.weatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        private val REQUIRED_PERMISSIONS_LOCATION =
            mutableListOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).toTypedArray()
        private const val REQUEST_CODE_LOCATION = 111
        private const val TAG = "DEV"
    }

    private var b: ActivityMainBinding? = null
    private lateinit var locationManager: FusedLocationProviderClient
    private var latitude = 0.0
    private var longitude = 0.0
    private var isDemo = false

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult.locations.isNotEmpty()) {
                val location = locationResult.lastLocation!!
                latitude = location.latitude
                longitude = location.longitude
                getWeatherInfo()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b?.root)

        locationManager = setupLocationProvider(this)

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

    private fun getCurrentLocation() {
        if (permissionsGranted()) {
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
        } else if (!isDemo) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS_LOCATION, REQUEST_CODE_LOCATION
            )
        } else {
            val snack = Snackbar.make(b?.tbMainToolbar!!,
                getString(R.string.sb_open_settings_location), Snackbar.LENGTH_LONG)
            snack.setAction(getString(R.string.sb_settings)) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            snack.show()
            //continue in demo mode unless the user has changed the permissions
            getWeatherInfo()
        }
    }

    private fun getWeatherInfo(){
       if (isNetworkEnabled(this)) {
           Log.i(TAG, "getWeatherInfo: Network OK")
       } else {
           Log.i(TAG, "getWeatherInfo: Network NOK")
       }
    }

    private fun permissionsGranted() = REQUIRED_PERMISSIONS_LOCATION.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(rC: Int, p: Array<out String>, gR: IntArray) {
        super.onRequestPermissionsResult(rC, p, gR)
        when(rC) {
            REQUEST_CODE_LOCATION -> {
                if (gR.isNotEmpty() && gR[0] == PackageManager.PERMISSION_GRANTED) {
                    isDemo = false
                    getCurrentLocation()
                } else {
                    isDemo = true
                    getCurrentLocation()
                }
                return
            }
            else -> return
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        b = null
    }
}