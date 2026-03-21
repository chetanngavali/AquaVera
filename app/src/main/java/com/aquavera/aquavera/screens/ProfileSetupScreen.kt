package com.aquavera.aquavera.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
    var name by remember { mutableStateOf(appViewModel.profile.name) }
    var surveyNo by remember { mutableStateOf(appViewModel.profile.survey_no) }
    var landArea by remember { mutableStateOf(if (appViewModel.profile.land_area > 0) appViewModel.profile.land_area.toString() else "") }
    var isAcres by remember { mutableStateOf(appViewModel.profile.area_unit == "Acres") }
    var state by remember { mutableStateOf(appViewModel.profile.state) }
    var district by remember { mutableStateOf(appViewModel.profile.district) }
    var taluka by remember { mutableStateOf(appViewModel.profile.taluka) }
    var village by remember { mutableStateOf(appViewModel.profile.village) }
    var plotNo by remember { mutableStateOf(appViewModel.profile.plot_no) }
    
    val t = langViewModel::t
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            AquaHeader(
                title = t("profile_setup"),
                onLanguageToggle = { langViewModel.toggleLanguage() },
                langLabel = when (langViewModel.currentLanguage) {
                    "en" -> "MR"
                    "mr" -> "HI"
                    else -> "EN"
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFC))
                .verticalScroll(scrollState)
        ) {
            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Step Indicator
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Step 1 of 1 · Profile setup",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        modifier = Modifier.size(70.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = "Complete your profile",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Add your details so we can calculate irrigation and billing accurately.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        lineHeight = 18.sp
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionTitle("Personal details", Icons.Default.Person)
                
                ProfileTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = t("full_name"),
                    icon = Icons.Default.Badge
                )

                SectionTitle("Land details", Icons.Default.Landscape)

                ProfileTextField(
                    value = surveyNo,
                    onValueChange = { surveyNo = it },
                    label = t("survey_no"),
                    icon = Icons.AutoMirrored.Filled.Assignment
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileTextField(
                        value = landArea,
                        onValueChange = { landArea = it },
                        label = t("land_area"),
                        icon = Icons.Default.AreaChart,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            UnitChip(selected = isAcres, label = t("acres"), onClick = { isAcres = true })
                            UnitChip(selected = !isAcres, label = t("hectares"), onClick = { isAcres = false })
                        }
                    }
                }

                SectionTitle("Location details", Icons.Default.LocationOn)

                ProfileTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = t("state"),
                    icon = Icons.Default.Map
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfileTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = t("district"),
                        modifier = Modifier.weight(1f)
                    )
                    ProfileTextField(
                        value = taluka,
                        onValueChange = { taluka = it },
                        label = t("taluka"),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfileTextField(
                        value = village,
                        onValueChange = { village = it },
                        label = t("village"),
                        modifier = Modifier.weight(1f)
                    )
                    ProfileTextField(
                        value = plotNo,
                        onValueChange = { plotNo = it },
                        label = t("plot_no"),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val area = landArea.toDoubleOrNull() ?: 0.0
                        appViewModel.updateProfile(
                            name = name,
                            surveyNo = surveyNo,
                            landArea = area,
                            areaUnit = if (isAcres) "Acres" else "Hectares",
                            state = state,
                            district = district,
                            taluka = taluka,
                            village = village,
                            plotNo = plotNo
                        )
                        onNavigateToDashboard()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    elevation = ButtonDefaults.buttonElevation(2.dp),
                    enabled = name.isNotEmpty() && surveyNo.isNotEmpty() && village.isNotEmpty()
                ) {
                    Text(t("save_and_continue"), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        leadingIcon = icon?.let { { Icon(it, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) } },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        ),
        keyboardOptions = keyboardOptions,
        singleLine = true
    )
}

@Composable
fun UnitChip(selected: Boolean, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}
