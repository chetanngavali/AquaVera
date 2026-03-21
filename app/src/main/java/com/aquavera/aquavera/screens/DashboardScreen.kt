package com.aquavera.aquavera.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquavera.aquavera.R
import com.aquavera.aquavera.ui.theme.*
import com.aquavera.aquavera.viewmodel.AppViewModel
import com.aquavera.aquavera.viewmodel.LangViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    appViewModel: AppViewModel,
    langViewModel: LangViewModel,
    onNavigateToMyRequests: () -> Unit,
    onNavigateToLandSummary: () -> Unit,
    onNavigateToProfileSummary: () -> Unit,
    onNavigateToBillSummary: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToRequestWater: () -> Unit
) {
    val profile = appViewModel.profile
    val requests = appViewModel.requests
    val pendingCount = requests.count { it.status == "Pending" }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(40.dp)
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(
                            badge = { Badge { Text("3") } }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = langViewModel.t("notifications"))
                        }
                    }
                    IconButton(onClick = onNavigateToProfileSummary) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = langViewModel.t("profile"), modifier = Modifier.size(20.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToRequestWater,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(langViewModel.t("request_water")) },
                containerColor = PrimaryGreen,
                contentColor = Color.White
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text(langViewModel.t("dashboard")) },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.WaterDrop, contentDescription = null) },
                    label = { Text(langViewModel.t("requests")) },
                    selected = false,
                    onClick = onNavigateToMyRequests
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(langViewModel.t("profile")) },
                    selected = false,
                    onClick = onNavigateToProfileSummary
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            item {
                Text(
                    langViewModel.t("operational_dashboard"),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    "${langViewModel.t("welcome_back")}, ${profile.name}",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Quick access buttons grid
            item {
                Text(
                    text = langViewModel.t("quick_access"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickAccessCard(
                            title = langViewModel.t("request_water"),
                            icon = Icons.Default.AddCircle,
                            color = Color(0xFFE0F2F1), // Light Teal
                            iconColor = Color(0xFF00695C), // Dark Teal for accessibility
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToRequestWater
                        )
                        QuickAccessCard(
                            title = langViewModel.t("land_summary"),
                            icon = Icons.Default.Landscape,
                            color = Color(0xFFE8F5E9), // Light Green
                            iconColor = Color(0xFF2E7D32), // Dark Green for accessibility
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToLandSummary
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickAccessCard(
                            title = langViewModel.t("bill_summary"),
                            icon = Icons.Default.ReceiptLong,
                            color = Color(0xFFFFF3E0), // Light Orange
                            iconColor = Color(0xFFE65100), // Dark Orange for accessibility
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToBillSummary
                        )
                        QuickAccessCard(
                            title = langViewModel.t("profile_summary"),
                            icon = Icons.Default.AccountBox,
                            color = Color(0xFFF3E5F5), // Light Purple
                            iconColor = Color(0xFF6A1B9A), // Dark Purple for accessibility
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToProfileSummary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(langViewModel.t("overview"), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = langViewModel.t("total_requests"),
                        value = requests.size.toString(),
                        trend = "",
                        icon = Icons.Default.Description,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = langViewModel.t("pending_approvals"),
                        value = pendingCount.toString(),
                        trend = "",
                        icon = Icons.Default.HourglassEmpty,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = langViewModel.t("total_bill"),
                        value = "₹${"%.2f".format(appViewModel.totalBill)}",
                        trend = "",
                        icon = Icons.Default.Payments,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = langViewModel.t("land_area"),
                        value = "${profile.land_area} ${profile.area_unit}",
                        trend = "",
                        icon = Icons.Default.Landscape,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(langViewModel.t("recent_requests"), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                if (requests.isEmpty()) {
                    Text(langViewModel.t("no_requests"), color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        requests.take(3).forEach { request ->
                            RequestListItem(request)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun QuickAccessCard(
    title: String,
    icon: ImageVector,
    color: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(100.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = iconColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RequestListItem(request: com.aquavera.aquavera.viewmodel.WaterRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(request.crop_type, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${request.season} • ${request.duration} Days", fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("₹${request.bill_amount}", fontWeight = FontWeight.Bold, color = PrimaryGreen)
                Text(
                    request.status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = when(request.status) {
                        "Approved" -> SuccessGreen
                        "Pending" -> Color(0xFFF59E0B)
                        else -> ErrorRed
                    }
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    trend: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    bgColor: Color = Color.White,
    iconTint: Color = PrimaryGreen
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                if (trend.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        trend,
                        fontSize = 12.sp,
                        color = if (trend.startsWith("+")) SuccessGreen else ErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
