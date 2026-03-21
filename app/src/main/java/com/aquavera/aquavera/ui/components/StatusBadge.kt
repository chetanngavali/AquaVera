package com.aquavera.aquavera.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aquavera.aquavera.ui.theme.SuccessGreen
import com.aquavera.aquavera.ui.theme.PendingYellow
import com.aquavera.aquavera.ui.theme.ErrorRed

@Composable
fun StatusBadge(status: String) {
    val (bg, textColor) = when (status) {
        "Approved" -> SuccessGreen.copy(alpha = 0.15f) to SuccessGreen
        "Pending" -> PendingYellow.copy(alpha = 0.15f) to PendingYellow
        else -> ErrorRed.copy(alpha = 0.15f) to ErrorRed
    }

    Text(
        text = status,
        color = textColor,
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
