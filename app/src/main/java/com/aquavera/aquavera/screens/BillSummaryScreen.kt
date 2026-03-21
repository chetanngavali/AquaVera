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
import androidx.compose.ui.unit.sp
import com.aquavera.aquavera.ui.components.BillCard
import com.aquavera.aquavera.viewmodel.AppViewModel
import com.aquavera.aquavera.viewmodel.LangViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillSummaryScreen(
    appViewModel: AppViewModel,
    langViewModel: LangViewModel,
    onBack: () -> Unit
) {
    val requests = appViewModel.requests
    val total = appViewModel.totalBill
    val paid = requests.filter { it.paid }.sumOf { it.bill_amount }
    val unpaid = total - paid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(langViewModel.t("bill_summary"), fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BillCard(
                    total = total,
                    paid = paid,
                    unpaid = unpaid,
                    t = langViewModel::t
                )
            }
            
            item {
                Text(langViewModel.t("billing_history"), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            items(requests) { request ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(request.crop_type, fontWeight = FontWeight.Bold)
                            Text(request.created_at, fontSize = 12.sp, color = androidx.compose.ui.graphics.Color.Gray)
                        }
                        Text(
                            "₹${request.bill_amount}",
                            fontWeight = FontWeight.ExtraBold,
                            color = if (request.paid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
