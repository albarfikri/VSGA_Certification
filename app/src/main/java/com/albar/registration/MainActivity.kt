package com.albar.registration

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.albar.registration.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Permission id location
    private var permissionId = 1000
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    // Permission id camera
    private val cameraRequestCode = 2000
    private val storageRequestCode = 2001

    // Image Pick
    private val imagePickCameraCode = 2002
    private val imagePickGalleryCode = 2003

    // arrays of permission
    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>

    // Variable to get Location
    private var locationDetected: String? = null

    // variables that will contain data to save in database
    private var nama: String? = null
    private var alamat: String? = null
    private var noHp: String? = null
    private var jenisKelamin: String? = null
    private var lokasi: String? = null
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Maps
        // Initiate fuse.providerclient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // add Event Button
        binding.btnLokasi.setOnClickListener {
            requestPermission()
            binding.tvLokasi.text = getString(R.string.loading)
            getLastLocation()
        }

        // Image
        binding.profile.setOnClickListener {
            // options to display
            val options = arrayOf("Camera", "Gallery")
            // settings dialog
            val builder = AlertDialog.Builder(this)
            // title
            builder.setTitle("Pick Image From")
            // set Item Options
            builder.setItems(options) { _, selected ->
                if (selected == 0) {
                    //camera clicked
                    if (!checkCameraPermissions()) {
                        // Permission not granted
                        requestCameraPermission()
                    } else {
                        // Permission already granted
                        pickFromCamera()
                    }
                } else {
                    //galery clicked
                    if (!checkStoragePermission()) {
                        requestStoragePermission()
                    } else {
                        pickFromGalery()
                    }
                }
            }
        }
    }

    private fun inputData() {
        nama = "" + binding.txtNama.text.toString()
        alamat = "" + binding.txtAlamat.text.toString()
        noHp = "" + binding.txtHp.text.toString()
        jenisKelamin = when (binding.rgJenisKelamin.checkedRadioButtonId) {
            R.id.rbPria -> "Pria"
            R.id.rbWanita -> "Wanita"
            else -> ""
        }
        lokasi = locationDetected
        image
    }


    private fun pickFromGalery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(
            galleryIntent,
            imagePickGalleryCode
        )
    }

    private fun requestStoragePermission() {
        // request the storage permission
        ActivityCompat.requestPermissions(this, storagePermissions, storageRequestCode)
    }

    private fun checkStoragePermission(): Boolean {
        // Check if storage permission is enabled or not
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun pickFromCamera() {
        // pick image from camera using intent
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Image Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description")
        //put image uri
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        // intent at open camera
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(
            cameraIntent,
            imagePickCameraCode
        )
    }

    private fun checkCameraPermissions(): Boolean {
        // Check if camera permissions (Camera and Storage) are enabled or not
        val results = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val results1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        return results && results1
    }

    private fun requestCameraPermission() {
        // request the camera permissions
        ActivityCompat.requestPermissions(this, cameraPermissions, cameraRequestCode)
    }

    private fun initCameraPermissions() {
        cameraPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                        locationDetected = address
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
            locationDetected = address
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