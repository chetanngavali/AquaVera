package com.aquavera.aquavera.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.aquavera.aquavera.ui.components.AquaHeader
import com.aquavera.aquavera.utils.Billing
import com.aquavera.aquavera.viewmodel.AppViewModel
import com.aquavera.aquavera.viewmodel.LangViewModel
import com.aquavera.aquavera.viewmodel.WaterRequest
import com.google.android.gms.location.LocationServices
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestFormScreen(
    appViewModel: AppViewModel,
    langViewModel: LangViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var cropType by remember { mutableStateOf("Rice") }
    var season by remember { mutableStateOf("Kharif") }
    var duration by remember { mutableStateOf("") }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var locationInfo by remember { mutableStateOf<Location?>(null) }
    var locationName by remember { mutableStateOf<String?>(null) }
    
    var isSubmitting by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf<WaterRequest?>(null) }

    val t = langViewModel::t
    val profile = appViewModel.profile

    fun fetchLocationName(location: Location) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val locality = address.locality ?: address.subLocality ?: ""
                val adminArea = address.adminArea ?: ""
                locationName = if (locality.isNotEmpty()) "$locality, $adminArea" else adminArea
            }
        } catch (e: Exception) {
            locationName = "Location name unavailable"
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                locationInfo = location
                location?.let { fetchLocationName(it) }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedImageUri = tempImageUri
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createTempPictureUri(context)
            tempImageUri = uri
            cameraLauncher.launch(uri)
        }
    }

    val liveBill = remember(season, duration) {
        val d = duration.toIntOrNull() ?: 0
        Billing.calculateBill(season, d, profile.land_area)
    }

    Scaffold(
        topBar = {
            AquaHeader(
                title = t("request_water"),
                onBack = onBack,
                onLanguageToggle = { langViewModel.toggleLanguage() },
                langLabel = if (langViewModel.currentLanguage == "en") "MR" else "EN"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(t("crop_type"), fontWeight = FontWeight.Bold)
            val crops = listOf("Rice", "Wheat", "Sugarcane", "Cotton")
            Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                crops.forEach { crop ->
                    FilterChip(
                        selected = cropType == crop,
                        onClick = { cropType = crop },
                        label = { Text(crop) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(t("season"), fontWeight = FontWeight.Bold)
            val seasons = listOf("Kharif", "Rabi", "Summer")
            Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                seasons.forEach { s ->
                    FilterChip(
                        selected = season == s,
                        onClick = { season = s },
                        label = { Text(t(s.lowercase())) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = duration,
                onValueChange = { if (it.all { char -> char.isDigit() }) duration = it },
                label = { Text(t("duration") + " (Days)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Capture Farm Image", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Please capture a clear photo of your plant for geo-tagging.", fontSize = 12.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clickable {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                val uri = createTempPictureUri(context)
                                tempImageUri = uri
                                cameraLauncher.launch(uri)
                            }
                            else -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (capturedImageUri == null) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text(t("capture_picture"))
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = rememberAsyncImagePainter(capturedImageUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        if (locationInfo != null) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    if (locationName != null) {
                                        Text(
                                            text = locationName!!,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "Lat: ${"%.4f".format(locationInfo?.latitude)}, Long: ${"%.4f".format(locationInfo?.longitude)}",
                                            color = Color.White,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Text(
                                        "Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}",
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(t("billing"), modifier = Modifier.weight(1f))
                    Text("₹$liveBill", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    isSubmitting = true
                    appViewModel.addRequest(
                        cropType,
                        season,
                        duration.toIntOrNull() ?: 0
                    ) { request ->
                        isSubmitting = false
                        showResultDialog = request
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isSubmitting && duration.isNotEmpty() && capturedImageUri != null
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(t("submit"), fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showResultDialog != null) {
        val request = showResultDialog!!
        val isApproved = request.status == "Approved"
        
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = onBack) {
                    Text("OK")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isApproved) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isApproved) Color(0xFF27AE60) else Color(0xFFF39C12)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isApproved) "Approved" else "Needs Review")
                }
            },
            text = {
                Column {
                    Text("Your request for ${request.crop_type} has been geo-tagged and submitted.")
                    if (locationName != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Location: $locationName", fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Bill Amount: ₹${request.bill_amount}", fontWeight = FontWeight.Bold)
                    if (locationInfo != null) {
                        Text("GPS Verified: Yes", color = Color(0xFF27AE60))
                    }
                }
            }
        )
    }
}

private fun createTempPictureUri(context: Context): Uri {
    val tempFile = File.createTempFile(
        "farm_${System.currentTimeMillis()}",
        ".jpg",
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}
