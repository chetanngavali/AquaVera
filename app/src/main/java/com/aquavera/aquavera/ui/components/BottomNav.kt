package com.aquavera.aquavera.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.aquavera.aquavera.viewmodel.LangViewModel

@Composable
fun AquaBottomNav(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    langViewModel: LangViewModel
) {
    val t = langViewModel::t
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(t("dashboard")) },
            selected = currentRoute == "dashboard",
            onClick = { onNavigate("dashboard") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            label = { Text(t("requests")) },
            selected = currentRoute == "requests",
            onClick = { /* Navigate to Requests list if separate */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = null) },
            label = { Text(t("billing")) },
            selected = currentRoute == "billing",
            onClick = { /* Navigate to Billing if separate */ }
        )
    }
}
