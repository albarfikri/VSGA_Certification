package com.albar.registration

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.albar.registration.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Permission id location
    private var permissionId = 1000
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    // Storage Id
    private val storageRequestCode = 2001

    // Image Pick
    private val imagePickGalleryCode = 2003

    // arrays of permission
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

    lateinit var dbHelper: DbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init db
        dbHelper = DbHelper(this)

        // Maps
        // Initiate fuse.providerclient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // add Event Button
        binding.btnLokasi.setOnClickListener {
            requestPermission()
            binding.tvLokasi.text = getString(R.string.loading)
            getLastLocation()
        }

        // init Storage Permission
        initStoragePermissions()

        binding.profile.setOnClickListener {
            selectImage()
        }

        binding.btnSubmit.setOnClickListener {
            inputData()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflate menu
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.record -> {
                val mIntent = Intent(this@MainActivity, RecordActivity::class.java)
                startActivity(mIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun selectImage() {
        //galery clicked
        if (!checkStoragePermission()) {
            requestStoragePermission()
        } else {
            pickFromGalery()
        }
    }

    private fun clear() {
        binding.txtNama.text = null
        binding.txtAlamat.text = null
        binding.txtHp.text = null
        binding.rgJenisKelamin.isEnabled = false
        lokasi = null
        binding.txtNama.clearFocus()
        binding.txtNama.clearFocus()
        binding.txtAlamat.clearFocus()
        binding.txtHp.clearFocus()
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

        // save data to db
        val timeStamp = System.currentTimeMillis()
        val id = dbHelper.insertData(
            "" + nama,
            "" + alamat,
            "" + noHp,
            "" + jenisKelamin,
            "" + lokasi,
            "" + imageUri,
            "" + timeStamp
        )
        Toast.makeText(this, "Record Added : $id", Toast.LENGTH_SHORT).show()
        clear()
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


    private fun initStoragePermissions() {
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
        when (requestCode) {
            permissionId -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Debug:", "You have got the permission")
                } else {
                    Toast.makeText(
                        this,
                        "You need to get gps Access",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            storageRequestCode -> {
                if (grantResults.isNotEmpty()) {
                    // if allowed returns true otherwise false
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted) {
                        pickFromGalery()
                    } else {
                        Toast.makeText(
                            this,
                            "Storage permission is required",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //image picked from camera or gallery will be received here
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == imagePickGalleryCode) {
                //picked from gallery
                //crop image
                CropImage.activity(data!!.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                //cropped image received
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri = result.uri
                    imageUri = resultUri
                    // set Image
                    binding.profile.setImageURI(resultUri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    //error
                    val error = result.error
                    Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

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

    // function to get city
    private fun getCity(lat: Double, long: Double): String {
        var cityName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, long, 1)
        cityName = address[0].locality

        return cityName
    }

    // function to get country
    private fun getCountryName(lat: Double, long: Double): String {
        var countryName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, long, 1)
        countryName = address[0].countryName

        return countryName
    }
}