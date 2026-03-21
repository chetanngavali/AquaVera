package com.aquavera.aquavera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.aquavera.aquavera.databinding.ActivityRequestWaterBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RequestWaterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestWaterBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private var isImageCaptured = false
    private var capturedLocation: Location? = null
    private var currentPhotoUri: Uri? = null
    private var currentPhotoPath: String? = null
    private var selectedSeason: String? = null

    private val cropsByType = mapOf(
        "Flowers" to listOf("Marigold", "Rose", "Jasmine", "Chrysanthemum", "Gerbera"),
        "Fodder Crops" to listOf("Berseem", "Lucerne", "Napier Grass", "Fodder Maize"),
        "Cereals" to listOf("Jowar", "Bajra", "Rice", "Wheat", "Maize"),
        "Pulses" to listOf("Tur", "Chana", "Moong", "Urad", "Masoor"),
        "Cash Crops" to listOf("Sugarcane", "Cotton", "Soybean"),
        "Oilseeds" to listOf("Groundnut", "Sunflower", "Mustard", "Sesame", "Safflower"),
        "Fruits" to listOf("Mango", "Banana", "Grapes", "Orange", "Pomegranate", "Papaya", "Guava", "Custard Apple"),
        "Vegetables" to listOf("Onion", "Tomato", "Potato", "Brinjal", "Cabbage", "Cauliflower", "Bhindi", "Chilli"),
        "Spices & Plantation" to listOf("Turmeric", "Ginger", "Chilli", "Coriander")
    )

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                binding.imgFarm.apply {
                    imageTintList = null
                    colorFilter = null
                    setPadding(0, 0, 0, 0)
                    scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                    setImageURI(null) // Clear first
                    setImageURI(uri)
                }
                isImageCaptured = true
                fetchLocation()
            }
        } else {
            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
        validateInputs()
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val locationGranted = (permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false) || 
                          (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false)

        if (cameraGranted && locationGranted) {
            launchCamera()
        } else {
            Toast.makeText(this, "Camera and Location permissions are required", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestWaterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupToolbar()
        setupExposedDropdowns()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        checkLocationStatus()
    }

    private fun checkLocationStatus() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val locationPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!isGpsEnabled || !locationPerm) {
            binding.autoCropType.isEnabled = false
            binding.autoCrop.isEnabled = false
            binding.toggleSeason.isEnabled = false
            binding.btnCaptureImage.isEnabled = false
            
            binding.tvLocation.visibility = View.VISIBLE
            binding.tvLocation.text = "⚠️ Turn on location to edit land details and fetch latest data"
            binding.tvLocation.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            
            // Re-using capture button area for enabling location if not enabled
            if (!isGpsEnabled) {
                binding.btnCaptureImage.isEnabled = true
                binding.btnCaptureImage.text = "Enable Location"
                binding.btnCaptureImage.setOnClickListener {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            } else if (!locationPerm) {
                binding.btnCaptureImage.isEnabled = true
                binding.btnCaptureImage.text = "Grant Permission"
                binding.btnCaptureImage.setOnClickListener {
                    requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                }
            }
        } else {
            // Restore normal behavior
            binding.autoCropType.isEnabled = true
            // binding.autoCrop is managed by autoCropType selection
            binding.toggleSeason.isEnabled = true
            binding.btnCaptureImage.isEnabled = true
            binding.btnCaptureImage.text = getString(R.string.btn_capture)
            binding.btnCaptureImage.setOnClickListener { checkPermissionsAndOpen() }
            
            if (!isImageCaptured) {
                binding.tvLocation.visibility = View.GONE
            } else {
                binding.tvLocation.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))
            }
        }
        validateInputs()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { 
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupExposedDropdowns() {
        val cropTypes = listOf("Select crop type") + cropsByType.keys.toList()
        val typeAdapter = ArrayAdapter(this, R.layout.spinner_item_light, cropTypes).also {
            it.setDropDownViewResource(R.layout.spinner_dropdown_item_light)
        }
        binding.autoCropType.setAdapter(typeAdapter)

        binding.autoCropType.setOnItemClickListener { _, _, position, _ ->
            val selectedType = cropTypes[position]
            if (selectedType != "Select crop type") {
                updateCropDropdown(selectedType)
            } else {
                binding.tilCrop.isEnabled = false
                binding.autoCrop.setText("", false)
            }
            validateInputs()
        }

        binding.autoCrop.setOnItemClickListener { _, _, _, _ ->
            validateInputs()
        }
    }

    private fun updateCropDropdown(type: String) {
        val crops = cropsByType[type] ?: emptyList()
        val cropAdapter = ArrayAdapter(this, R.layout.spinner_item_light, crops).also {
            it.setDropDownViewResource(R.layout.spinner_dropdown_item_light)
        }
        binding.autoCrop.setAdapter(cropAdapter)
        binding.autoCrop.setText("", false)
        binding.tilCrop.isEnabled = true
        binding.autoCrop.postDelayed({ binding.autoCrop.showDropDown() }, 100)
    }

    private fun setupListeners() {
        binding.toggleSeason.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                selectedSeason = when (checkedId) {
                    R.id.btnKharif -> "Kharif"
                    R.id.btnRabi -> "Rabi"
                    R.id.btnSummer -> "Summer"
                    else -> null
                }
                updateBilling()
            } else if (group.checkedButtonId == View.NO_ID) {
                selectedSeason = null
            }
            validateInputs()
        }

        binding.btnCaptureImage.setOnClickListener {
            checkPermissionsAndOpen()
        }

        binding.btnSubmit.setOnClickListener {
            val type = binding.autoCropType.text.toString()
            val crop = binding.autoCrop.text.toString()
            val season = selectedSeason ?: "None"
            val loc = if (capturedLocation != null) "Lat: ${capturedLocation?.latitude}, Lon: ${capturedLocation?.longitude}" else "No location"
            Toast.makeText(this, "Crop: $crop, Season: $season, Loc: $loc", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermissionsAndOpen() {
        val cameraPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val locationPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (cameraPerm == PackageManager.PERMISSION_GRANTED && locationPerm == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
        }
    }

    private fun launchCamera() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: Exception) {
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                it
            )
            currentPhotoUri = photoURI
            takePictureLauncher.launch(photoURI)
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                capturedLocation = location
                updateGeoTagDisplay(location)
                validateInputs()
            } else {
                Toast.makeText(this, "Unable to get location. Ensure GPS is ON.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateGeoTagDisplay(location: Location) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val now = Date()
        val date = dateFormat.format(now)
        val time = timeFormat.format(now)
        
        val deviceInfo = "${Build.MANUFACTURER} ${Build.MODEL}"
        val address = getAddressFromLocation(location)
        
        val direction = if (location.hasBearing()) {
            String.format(Locale.getDefault(), "%.1f°", location.bearing)
        } else {
            "N/A"
        }

        val geoTagText = """
            📍 Lat: ${String.format(Locale.getDefault(), "%.4f", location.latitude)}, Lon: ${String.format(Locale.getDefault(), "%.4f", location.longitude)}
            📅 Date: $date  ⌚ Time: $time
            🏠 Location: $address
            📱 Device: $deviceInfo
            🧭 Direction: $direction
        """.trimIndent()

        binding.tvLocation.visibility = View.VISIBLE
        binding.tvLocation.text = geoTagText
        binding.tvLocation.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))
    }

    private fun getAddressFromLocation(location: Location): String {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                "Address not found"
            }
        } catch (e: Exception) {
            "Service unavailable"
        }
    }

    private fun updateBilling() {
        // Updated billing logic for seasons
        val price = when(selectedSeason) {
            "Kharif" -> 1500.0
            "Rabi" -> 1200.0
            "Summer" -> 800.0
            else -> 0.0
        }
        binding.tvBillingAmount.text = String.format(Locale.getDefault(), "₹%.1f", price)
    }

    private fun validateInputs() {
        val typeText = binding.autoCropType.text?.toString()
        val isTypeSelected = !typeText.isNullOrEmpty() && typeText != "Select crop type"
        val isCropSelected = binding.autoCrop.text?.isNotEmpty() == true
        val isSeasonSelected = selectedSeason != null
        
        val isValid = isTypeSelected && isCropSelected && isSeasonSelected && isImageCaptured && capturedLocation != null
        binding.btnSubmit.isEnabled = isValid
        
        if (isValid) {
            binding.btnSubmit.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.teal_accent))
            binding.btnSubmit.setTextColor(ContextCompat.getColor(this, R.color.dark_navy))
        } else {
            binding.btnSubmit.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.medium_grey))
            binding.btnSubmit.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }
}
