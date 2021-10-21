package com.albar.registration

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.albar.registration.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Permission id
    private var permissionId = 1000
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initiate fuse.providerclient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // add Event Button
        binding.btnLokasi.setOnClickListener {
            requestPermission()
            binding.tvLokasi.text = "Loading..."
            getLastLocation()
        }
    }

    // Checking user permission
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // Function that will allow us to get user permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), permissionId
        )
    }

    // Function that will check location service is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // this is a build in function that check the permission result
        if (requestCode == permissionId) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug:", "You have got the permission")
            }
        }
    }

    // function that will allow us to get the last location
    private fun getLastLocation() {
        // check permission
        if (checkPermission()) {
            // check location service is enabled
            if (isLocationEnabled()) {
                // get the location
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        // if the location is null we will get the new user location
                        // adding new locatoin
                        getNewLocation()
                    } else {
                        // location.latitude
                        val address =
                            getCity(location.latitude, location.longitude) + ", " + getCountryName(
                                location.latitude,
                                location.longitude
                            )
                        binding.tvLokasi.text = address
                    }
                }
            } else {
                Toast.makeText(this, "Please enable your location service", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
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
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation: Location = p0.lastLocation
            val address =
                getCity(lastLocation.latitude, lastLocation.longitude) + ", " + getCountryName(
                    lastLocation.latitude,
                    lastLocation.longitude
                )
            binding.tvLokasi.text = address
        }
    }

    // function to get the address
    private fun getCity(lat: Double, long: Double): String {
        var cityName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, long, 1)
        cityName = address[0].locality

        return cityName
    }

    private fun getCountryName(lat: Double, long: Double): String {
        var countryName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, long, 1)
        countryName = address[0].countryName

        return countryName
    }

}