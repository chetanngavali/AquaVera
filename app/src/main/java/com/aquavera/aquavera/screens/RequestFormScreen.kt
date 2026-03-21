package com.aquavera.aquavera.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.aquavera.aquavera.ui.components.AquaHeader
import com.aquavera.aquavera.utils.Billing
import com.aquavera.aquavera.viewmodel.AppViewModel
import com.aquavera.aquavera.viewmodel.LangViewModel
import com.aquavera.aquavera.viewmodel.WaterRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestFormScreen(
    appViewModel: AppViewModel,
    langViewModel: LangViewModel,
    onBack: () -> Unit
) {
    var cropType by remember { mutableStateOf("Rice") }
    var season by remember { mutableStateOf("Kharif") }
    var duration by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf<WaterRequest?>(null) }

    val t = langViewModel::t
    val profile = appViewModel.profile
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val liveBill = remember(season, duration) {
        val d = duration.toIntOrNull() ?: 0
        Billing.calculateBill(season, d, profile.landArea)
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
            // Dropdown simulation for Crop Type
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
                onValueChange = { duration = it },
                label = { Text(t("duration")) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable { launcher.launch("image/*") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (imageUri == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(t("upload_image"))
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
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
                        duration.toIntOrNull() ?: 0,
                        imageUri
                    ) { request ->
                        isSubmitting = false
                        showResultDialog = request
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isSubmitting && duration.isNotEmpty()
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
                    Text("Your request for ${request.cropType} has been processed.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Bill Amount: ₹${request.billAmount}", fontWeight = FontWeight.Bold)
                    if (!isApproved) {
                        Text("An official will visit for crop verification soon.", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )
    }
}
