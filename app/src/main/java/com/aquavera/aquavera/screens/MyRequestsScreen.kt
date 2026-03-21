package com.aquavera.aquavera.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aquavera.aquavera.ui.components.RequestCard
import com.aquavera.aquavera.viewmodel.AppViewModel
import com.aquavera.aquavera.viewmodel.LangViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRequestsScreen(
    appViewModel: AppViewModel,
    langViewModel: LangViewModel,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(langViewModel.t("my_requests"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = langViewModel.t("back"))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(appViewModel.requests) { request ->
                RequestCard(request = request)
            }
        }
    }
}
