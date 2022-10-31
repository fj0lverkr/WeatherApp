package com.nilsnahooy.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nilsnahooy.weatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var b: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        b = null
    }
}