package com.nilsnahooy.weatherapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.Manifest
import android.net.Uri
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.nilsnahooy.weatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        private val REQUIRED_PERMISSIONS_LOCATION =
            mutableListOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).toTypedArray()
        private val REQUIRED_PERMISSIONS_NETWORK =
            mutableListOf(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
            ).toTypedArray()
        private const val REQUEST_CODE_LOCATION = 111
        private const val REQUEST_CODE_NETWORK = 222
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

    private fun getCurrentLocation() {
        if (permissionsGranted(REQUIRED_PERMISSIONS_LOCATION)) {
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
            b?.tvTemp?.text = "[DEMO] lat: $latitude long: $longitude"
        }
    }

    private fun setUpNetwork(){
        if(permissionsGranted(REQUIRED_PERMISSIONS_NETWORK)) {

        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS_NETWORK, REQUEST_CODE_NETWORK
            )
        }
    }

    private fun permissionsGranted(permissions: Array<String>) = permissions.all {
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
            REQUEST_CODE_NETWORK -> {
                if (gR.isNotEmpty() && gR[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpNetwork()
                } else {
                    Toast.makeText(this, getString(R.string.tst_no_network_permission),
                        Toast.LENGTH_LONG).show()
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