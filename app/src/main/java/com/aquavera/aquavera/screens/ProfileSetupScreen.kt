package com.aquavera.aquavera.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquavera.aquavera.R
import com.aquavera.aquavera.ui.components.AquaHeader
import com.aquavera.aquavera.viewmodel.AppViewModel
import com.aquavera.aquavera.viewmodel.LangViewModel

@Composable
fun ProfileSetupScreen(
    appViewModel: AppViewModel,
    langViewModel: LangViewModel,
    onNavigateToDashboard: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var aadhaar by remember { mutableStateOf("") }
    var landId by remember { mutableStateOf("") }
    var landArea by remember { mutableStateOf("") }
    var isAcres by remember { mutableStateOf(true) }
    
    val t = langViewModel::t

    Scaffold(
        topBar = {
            AquaHeader(
                title = t("profile_setup"),
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
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(t("full_name")) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = aadhaar,
                onValueChange = { if (it.length <= 4) aadhaar = it },
                label = { Text(t("aadhaar")) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = landId,
                onValueChange = { landId = it },
                label = { Text(t("land_id")) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = landArea,
                    onValueChange = { landArea = it },
                    label = { Text(t("land_area")) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = isAcres, onClick = { isAcres = true })
                        Text(t("acres"))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = !isAcres, onClick = { isAcres = false })
                        Text(t("hectares"))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val area = landArea.toDoubleOrNull() ?: 0.0
                    appViewModel.updateProfile(name, aadhaar, landId, area, if (isAcres) "Acres" else "Hectares")
                    onNavigateToDashboard()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(t("save"), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
